#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Генерирует Flyway data-миграцию, наполняющую public.faculties по данным, собранным в
explore/faculties/*.md (см. fetch_faculties_ucheba.py) -- продолжение issue #73.

Что делает скрипт:
  1. Парсит каждый explore/faculties/<slug>.md -- список факультетов/институтов вуза.
  2. Находит university_id -- парсит уже существующие миграции с "INSERT INTO
     public.universities" (V2, V17, V19 и любые другие data-миграции, где встречается эта
     таблица) и строит соответствие "нормализованное краткое/полное название -> id", беря
     краткое название из explore/<slug>.md (то же поле "Сокращённое название", что и
     использовал generate_universities_migration.py при заполнении universities.name).
  3. Пропускает вуз, если university_id не нашёлся (не выдумываем связь) -- логирует в
     stderr, чтобы было видно, что осталось разобрать вручную.
  4. Учитывает ограничение схемы UNIQUE (name, university_id) на faculties
     (V1__create_university_schema.sql) -- дедуплицирует одинаковые названия факультетов в
     рамках одного вуза перед генерацией.
  5. Определяет следующий свободный номер миграции по обеим подпапкам schema/ и data/ (как
     того требует CLAUDE.md) и пишет туда INSERT INTO public.faculties.
  6. Как и другие генераторы -- отказывается перезаписывать уже существующий выходной файл
     без --force.

Запуск:
    py scripts/generate_faculties_migration.py
    py scripts/generate_faculties_migration.py --force
"""

import argparse
import re
import sys
import uuid
from pathlib import Path

ROOT = Path(__file__).parent.parent
EXPLORE_DIR = ROOT / "explore"
FACULTIES_DIR = EXPLORE_DIR / "faculties"
MIGRATIONS_DIR = ROOT / "src" / "main" / "resources" / "db" / "migration"

FIELD_RE = re.compile(r"^-\s*\*\*([^:*]+):\*\*\s*(.*)$")
UNIT_LINE_RE = re.compile(r"^-\s+(.*)$")
UNIVERSITY_INSERT_RE = re.compile(
    r"^INSERT INTO public\.universities \(([^)]*)\) VALUES \((.*)\);\s*$", re.MULTILINE
)

STRIP_PREFIXES = [
    "федеральное государственное бюджетное образовательное учреждение высшего образования",
    "федеральное государственное автономное образовательное учреждение высшего образования",
    "федеральное государственное казённое образовательное учреждение высшего образования",
    "государственное образовательное учреждение высшего образования",
    "образовательное учреждение высшего образования",
]


def normalize(name: str) -> str:
    n = name.lower().strip()
    n = n.replace("«", '"').replace("»", '"').replace("'", '"')
    n = re.sub(r'["\(\)]', " ", n)
    for prefix in STRIP_PREFIXES:
        if n.startswith(prefix):
            n = n[len(prefix):].strip()
    n = re.sub(r"\s+", " ", n).strip().rstrip(".,")
    return n


def split_sql_values(values_str: str) -> list[str]:
    """Разбивает список значений VALUES(...) по запятым вне строк в кавычках."""
    parts = []
    current = []
    in_quotes = False
    i = 0
    while i < len(values_str):
        ch = values_str[i]
        if ch == "'":
            if in_quotes and i + 1 < len(values_str) and values_str[i + 1] == "'":
                current.append("''")
                i += 2
                continue
            in_quotes = not in_quotes
            current.append(ch)
        elif ch == "," and not in_quotes:
            parts.append("".join(current).strip())
            current = []
        else:
            current.append(ch)
        i += 1
    if current:
        parts.append("".join(current).strip())
    return [p.strip("'") if p.strip().startswith("'") else p.strip() for p in parts]


def load_university_ids() -> tuple[dict[str, str], dict[str, set[str]]]:
    """Строит два индекса по всем data-миграциям с public.universities:
      - by_full: normalized(description, т.е. полное название) -> university_id -- полное
        название специфично почти всегда, это основной, надёжный сигнал;
      - by_short: normalized(name, т.е. краткое название/аббревиатура) -> set(university_id) --
        аббревиатуры вроде "МГПУ" НЕ уникальны (Московский и Мордовский пед. университеты обе
        сокращаются как МГПУ) -- если множество из нескольких id, короткое имя нельзя
        использовать для однозначного сопоставления."""
    by_full: dict[str, str] = {}
    by_short: dict[str, set[str]] = {}
    for sql_path in sorted(MIGRATIONS_DIR.rglob("*.sql")):
        text = sql_path.read_text(encoding="utf-8", errors="replace")
        for m in UNIVERSITY_INSERT_RE.finditer(text):
            columns = [c.strip() for c in m.group(1).split(",")]
            values = split_sql_values(m.group(2))
            if len(values) != len(columns):
                continue
            row = dict(zip(columns, values))
            uid = row.get("id")
            name = row.get("name")
            description = row.get("description")
            if not uid:
                continue
            if name:
                norm_short = normalize(name.replace("''", "'"))
                if norm_short:
                    by_short.setdefault(norm_short, set()).add(uid)
            if description:
                norm_full = normalize(description.replace("''", "'"))
                if norm_full:
                    by_full.setdefault(norm_full, uid)
    return by_full, by_short


def resolve_university_id(
    short_name: str, full_name: str, by_full: dict[str, str], by_short: dict[str, set[str]]
) -> str | None:
    """Полное название -- основной сигнал (почти всегда уникально). Краткое название/
    аббревиатура используется только если оно однозначно (маппится ровно на один id) --
    иначе (например "МГПУ" -- два разных вуза) не угадываем, возвращаем None."""
    norm_full = normalize(full_name)
    if norm_full and norm_full in by_full:
        return by_full[norm_full]

    norm_short = normalize(short_name)
    if norm_short in by_short:
        ids = by_short[norm_short]
        if len(ids) == 1:
            return next(iter(ids))
        return None  # неоднозначная аббревиатура -- несколько разных вузов, не угадываем
    return None


def parse_explore_field(path: Path, field: str) -> str:
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m and m.group(1).strip() == field:
            return m.group(2).strip()
    return ""


def parse_faculties_file(path: Path) -> list[str]:
    names = []
    in_section = False
    for line in path.read_text(encoding="utf-8").splitlines():
        stripped = line.strip()
        if stripped.startswith("## "):
            in_section = stripped.startswith("## Факультеты")
            continue
        if not in_section:
            continue
        m = UNIT_LINE_RE.match(stripped)
        if m:
            text = m.group(1).strip()
            if "требует уточнения" in text:
                continue
            names.append(text)
    return names


def next_migration_version() -> int:
    versions = []
    for sql_path in MIGRATIONS_DIR.rglob("V*.sql"):
        m = re.match(r"V(\d+)__", sql_path.name)
        if m:
            versions.append(int(m.group(1)))
    return max(versions, default=0) + 1


def sql_escape(value: str) -> str:
    return value.replace("'", "''")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if not FACULTIES_DIR.exists():
        raise SystemExit(f"Нет {FACULTIES_DIR} -- сначала запустите fetch_faculties_ucheba.py")

    print("Строю справочник university_id по существующим миграциям...", file=sys.stderr)
    by_full, by_short = load_university_ids()
    print(f"Полных названий: {len(by_full)}, кратких: {len(by_short)}", file=sys.stderr)
    ambiguous_short = {k: v for k, v in by_short.items() if len(v) > 1}
    if ambiguous_short:
        print(
            f"Неоднозначных аббревиатур (несколько вузов с одним name): {len(ambiguous_short)} "
            f"-- {', '.join(sorted(ambiguous_short)[:10])}{' ...' if len(ambiguous_short) > 10 else ''}",
            file=sys.stderr,
        )

    inserts: list[str] = []
    matched = 0
    unmatched: list[str] = []
    total_faculties = 0
    seen_global: set[tuple[str, str]] = set()  # (university_id, normalized_name) -- защита от
    # дублей UNIQUE(name, university_id), включая случай, когда два explore-слага почему-то
    # разрешились в один и тот же university_id.

    for fac_path in sorted(FACULTIES_DIR.glob("*.md")):
        slug = fac_path.stem
        explore_path = EXPLORE_DIR / f"{slug}.md"
        if not explore_path.exists():
            unmatched.append(f"{slug} (нет исходного explore/{slug}.md)")
            continue

        short_name = parse_explore_field(explore_path, "Сокращённое название")
        full_name = parse_explore_field(explore_path, "Полное название")

        uid = resolve_university_id(short_name, full_name, by_full, by_short)
        if not uid:
            unmatched.append(slug)
            continue
        matched += 1

        faculty_names = parse_faculties_file(fac_path)
        for name in faculty_names:
            key = (uid, normalize(name))
            if key in seen_global:
                continue
            seen_global.add(key)
            fac_id = str(uuid.uuid4())
            inserts.append(
                f"INSERT INTO public.faculties (id, name, university_id) VALUES "
                f"('{fac_id}', '{sql_escape(name)}', '{uid}');"
            )
            total_faculties += 1

    print(f"Сматчено вузов: {matched}, не найден university_id: {len(unmatched)}", file=sys.stderr)
    if unmatched:
        print("Не сматчены (пропущены): " + ", ".join(unmatched), file=sys.stderr)
    print(f"Всего факультетов/институтов к вставке: {total_faculties}", file=sys.stderr)

    if not inserts:
        print("Нечего генерировать.", file=sys.stderr)
        return

    version = next_migration_version()
    out_path = MIGRATIONS_DIR / "data" / f"V{version}__insert_faculties_bulk.sql"

    if out_path.exists() and not args.force:
        raise SystemExit(f"{out_path} уже существует -- используйте --force для пересборки")

    header = (
        "-- Факультеты/институты для вузов из issue #73, собранные с ucheba.ru "
        "(explore/faculties/*.md).\n"
        "-- Сгенерировано scripts/generate_faculties_migration.py, см. также "
        "fetch_faculties_ucheba.py и\n"
        "-- match_universities_to_ucheba.py -- по части вузов список неполный "
        "(указано в explore/faculties/*.md\n"
        '-- пометкой "требует уточнения"), полные учебные программы (как у ДГТУ, '
        "V5) не собирались.\n\n"
    )
    out_path.write_text(header + "\n".join(inserts) + "\n", encoding="utf-8")
    print(f"Записано -> {out_path}", file=sys.stderr)


if __name__ == "__main__":
    main()
