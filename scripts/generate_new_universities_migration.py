#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Генерирует Flyway data-миграцию, наполняющую public.universities (и public.address -- там, где
город известен) вузами/филиалами, добавленными в explore/*.md с vuzopedia.ru (см.
generate_new_universities_from_vuzopedia.py) -- продолжение issue #73.

В отличие от generate_universities_migration.py / generate_remaining_universities_migration.py:
  - у этих записей почти никогда нет ни сайта (email всегда NULL), ни точного города --
    профильная страница vuzopedia.ru прячет адрес/сайт за кнопками "посмотреть" (см.
    scripts/README.md);
  - раньше (до V18__make_university_address_id_nullable.sql) вуз без адреса нельзя было
    вставить вовсе -- universities.address_id был NOT NULL. Теперь можно: там, где город
    определился (см. generate_new_universities_from_vuzopedia.py, CITY_REGION), пишем
    адрес как "г. Город, Регион" (по аналогии с fallback в generate_universities_migration.py);
    там, где город неизвестен -- address_id оставляем NULL, а не выдумываем.

Запуск:
    py scripts/generate_new_universities_migration.py
    py scripts/generate_new_universities_migration.py --force
"""

import argparse
import re
import sys
import uuid
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
from generate_universities_migration import CITY_REGION, UNKNOWN_MARK, sql_int, sql_str  # noqa: E402

ROOT = Path(__file__).parent.parent
EXPLORE_DIR = ROOT / "explore"
MIGRATIONS_DIR = ROOT / "src" / "main" / "resources" / "db" / "migration"

FIELD_RE = re.compile(r"^-\s*\*\*([^:*]+):\*\*\s*(.*)$")
YEAR_RE = re.compile(r"\b(1[5-9]\d{2}|20[0-2]\d)\b")
DIGITS_RE = re.compile(r"(\d[\d\s ]*\d|\d)")
NEW_FILE_RE = re.compile(r"^(\d+)-")

# Первый номер explore-файла, добавленного с vuzopedia.ru (см.
# generate_new_universities_from_vuzopedia.py) -- отделяет новые записи от исходных 700.
FIRST_NEW_FILE_NUMBER = 701


def parse_new_file(path: Path) -> dict:
    fields = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m:
            fields[m.group(1).strip()] = m.group(2).strip()

    full_name = fields.get("Полное название") or path.stem
    short_name = fields.get("Сокращённое название", "")
    raw_city = fields.get("Город", "")
    raw_year = fields.get("Год основания", "")
    raw_rector = fields.get("Ректор", "")
    raw_students = fields.get("Число студентов", "")

    name = short_name if (short_name and UNKNOWN_MARK not in short_name) else full_name

    year_match = YEAR_RE.search(raw_year) if UNKNOWN_MARK not in raw_year else None
    founding_year = int(year_match.group(1)) if year_match else None

    rector = None if (not raw_rector or UNKNOWN_MARK in raw_rector) else raw_rector[:255]

    student_count = None
    if raw_students and UNKNOWN_MARK not in raw_students:
        digits_match = DIGITS_RE.search(raw_students)
        if digits_match:
            student_count = int(re.sub(r"[\s ]", "", digits_match.group(1)))

    city_region = CITY_REGION.get(raw_city) if raw_city and UNKNOWN_MARK not in raw_city else None

    return {
        "file": path.name,
        "name": name[:255],
        "description": full_name,
        "city_region": city_region,  # (city, region) или None
        "rector": rector,
        "founding_year": founding_year,
        "student_count": student_count,
    }


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    new_files = [
        p
        for p in sorted(EXPLORE_DIR.glob("*.md"))
        if (m := NEW_FILE_RE.match(p.name)) and int(m.group(1)) >= FIRST_NEW_FILE_NUMBER
    ]
    print(f"Новых explore-файлов (>= {FIRST_NEW_FILE_NUMBER}): {len(new_files)}")

    records = [parse_new_file(p) for p in new_files]
    with_address = [r for r in records if r["city_region"]]
    without_address = [r for r in records if not r["city_region"]]
    print(f"С определённым городом (будет адрес): {len(with_address)}")
    print(f"Без города (address_id = NULL): {len(without_address)}")

    out_dir = MIGRATIONS_DIR / "data"
    versions = [int(m.group(1)) for p in MIGRATIONS_DIR.rglob("V*.sql") if (m := re.match(r"V(\d+)__", p.name))]
    next_version = max(versions, default=0) + 1
    out_path = out_dir / f"V{next_version}__insert_new_universities_from_vuzopedia.sql"

    if out_path.exists() and not args.force:
        print(f"{out_path} уже существует -- используйте --force", file=sys.stderr)
        sys.exit(1)

    lines = [
        "-- Вузы и филиалы, добавленные в explore/*.md с vuzopedia.ru (issue #73, продолжение) --",
        "-- см. scripts/generate_new_universities_from_vuzopedia.py и scripts/README.md.",
        "-- У большинства нет ни сайта (email/website = NULL), ни точного города: там, где город",
        "-- определился по названию -- есть address (город/регион, без точной улицы); где нет --",
        "-- address_id = NULL (см. V18__make_university_address_id_nullable.sql). Ректор/год",
        "-- основания/число студентов -- NULL везде, кроме единичных случаев, где были в тексте",
        "-- названия (в подавляющем большинстве эти поля скрыты источником, не выдумываем).",
        "",
    ]

    for r in records:
        uni_id = str(uuid.uuid4())
        addr_id = None
        if r["city_region"]:
            city, region = r["city_region"]
            addr_id = str(uuid.uuid4())
            lines.append(
                "INSERT INTO public.address (id, address, country, region, city, street) VALUES ("
                f"{sql_str(addr_id)}, {sql_str(f'г. {city}, {region}')}, {sql_str('Россия')}, "
                f"{sql_str(region)}, {sql_str(city)}, {sql_str(f'г. {city}')});"
            )
        lines.append(
            "INSERT INTO public.universities (id, name, description, address_id, rector, "
            "founding_year, student_count) VALUES ("
            f"{sql_str(uni_id)}, {sql_str(r['name'])}, {sql_str(r['description'])}, "
            f"{sql_str(addr_id) if addr_id else 'NULL'}, {sql_str(r['rector'])}, "
            f"{sql_int(r['founding_year'])}, {sql_int(r['student_count'])});"
        )
        lines.append("")

    out_path.write_text("\n".join(lines), encoding="utf-8", newline="\n")
    print(f"Записано {out_path.relative_to(ROOT)} ({len(records)} вузов, {len(with_address)} с адресом)")


if __name__ == "__main__":
    main()
