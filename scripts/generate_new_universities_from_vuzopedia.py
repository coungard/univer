#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Дополняет explore/*.md вузами из индекса vuzopedia.ru (scripts/vuzopedia_id_index.jsonl, 1708
записей, см. build_vuzopedia_id_index.py), которых ещё нет среди наших 700 (issue #73).

ВАЖНО про качество данных: подавляющее большинство "новых" записей в каталоге vuzopedia.ru --
это филиалы/факультеты/школы уже известных вузов (например, "Филиал МГУ имени М.В.
Ломоносова в городе Севастополе"), которые vuzopedia.ru зачем-то каталогизирует как
отдельные записи `/vuz/{id}`. С этим смирились сознательно -- добавляем и филиалы тоже.

Дополнительное ограничение: профильная страница `/vuz/{id}` прячет адрес/сайт/email/
ректора/число студентов за кнопками "посмотреть" (подгружаются не статически) -- в
статическом HTML их просто нет. Поэтому у новых записей реально заполняются только:
  - Полное название (из индекса);
  - Город -- если в самом названии есть явный паттерн "г. X"/"в городе X", регионы
    определяются через тот же CITY_REGION, что и в generate_universities_migration.py, с
    fallback на сравнение по стемам слов (падеж в названии часто не именительный, "в г.
    Севастополе" -- предложный).
Всё остальное (Ректор, Год основания, Тип, точный адрес улицы, Число студентов) --
"информация требует уточнения", как и договаривались: не выдумываем.

Файлы нумеруются начиная со следующего свободного номера после уже существующих (701+).

Запуск:
    py scripts/generate_new_universities_from_vuzopedia.py
    py scripts/generate_new_universities_from_vuzopedia.py --limit 20   # пробный прогон
"""

import argparse
import json
import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
import generate_universities_migration as gum  # переиспользуем CITY_REGION

EXPLORE_DIR = Path(__file__).parent.parent / "explore"
VUZOPEDIA_INDEX_PATH = Path(__file__).parent / "vuzopedia_id_index.jsonl"

FIELD_RE = re.compile(r"^-\s*\*\*([^:*]+):\*\*\s*(.*)$")
CITY_PATTERN = re.compile(r"(?:в\s+(?:г\.|город[а-я]*)|г\.)\s*([А-ЯЁ][а-яёА-ЯЁ\-]+)")
STEM_LEN = 5

# vuzopedia.ru вперемешку с вузами/филиалами каталогизирует и их ВНУТРЕННИЕ подразделения
# как отдельные записи /vuz/{id} (например, "Историко-филологический факультет
# Севастопольского филиала МГУ") -- это не вуз и не филиал сам по себе, а факультет уже
# учтённого (или ещё не учтённого) филиала. Такие записи не подходят под сущность
# University в схеме БД вообще (это Faculty), поэтому исключаем их целиком, а не просто
# помечаем как "требует уточнения".
SUB_ENTITY_RE = re.compile(r"факультет|кафедра", re.IGNORECASE)


def normalize(name: str) -> str:
    n = name.lower().strip()
    n = n.replace("«", '"').replace("»", '"')
    n = re.sub(r'["\(\)]', " ", n)
    n = re.sub(r"\s+", " ", n).strip().rstrip(".,")
    return n


def load_explore_names() -> set[str]:
    names = set()
    for p in EXPLORE_DIR.glob("*.md"):
        for line in p.read_text(encoding="utf-8").splitlines():
            m = FIELD_RE.match(line.strip())
            if m and m.group(1).strip() in ("Полное название", "Сокращённое название"):
                n = normalize(m.group(2).strip())
                if n:
                    names.add(n)
    return names


def build_city_stem_index() -> dict[str, tuple[str, str]]:
    """Первые STEM_LEN символов нормализованного названия города (без уточнения в скобках)
    -> (город, регион). Падеж в тексте вуза часто не совпадает с ключом CITY_REGION, поэтому
    матчим по короткому устойчивому к падежам префиксу, а не по точному имени."""
    index: dict[str, tuple[str, str]] = {}
    for key, value in gum.CITY_REGION.items():
        base_city = key.split("(")[0].split(",")[-1].strip()
        stem = base_city.lower()[:STEM_LEN]
        if stem and stem not in index:
            index[stem] = value
    return index


def extract_city(name: str, city_stem_index: dict[str, tuple[str, str]]) -> tuple[str, str] | None:
    m = CITY_PATTERN.search(name)
    if not m:
        return None
    candidate = m.group(1)
    stem = candidate.lower()[:STEM_LEN]
    return city_stem_index.get(stem)


def slugify(name: str, vid: str) -> str:
    translit = {
        "а": "a", "б": "b", "в": "v", "г": "g", "д": "d", "е": "e", "ё": "e", "ж": "zh",
        "з": "z", "и": "i", "й": "i", "к": "k", "л": "l", "м": "m", "н": "n", "о": "o",
        "п": "p", "р": "r", "с": "s", "т": "t", "у": "u", "ф": "f", "х": "h", "ц": "c",
        "ч": "ch", "ш": "sh", "щ": "sch", "ъ": "", "ы": "y", "ь": "", "э": "e", "ю": "yu",
        "я": "ya",
    }
    lower = name.lower()
    out = "".join(translit.get(ch, ch if ch.isalnum() else "-") for ch in lower)
    out = re.sub(r"-+", "-", out).strip("-")
    words = [w for w in out.split("-") if w][:4]
    slug = "-".join(words) if words else f"vuz-{vid}"
    return slug[:60]


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--limit", type=int, default=None)
    args = parser.parse_args()

    if not VUZOPEDIA_INDEX_PATH.exists():
        raise SystemExit(f"Нет {VUZOPEDIA_INDEX_PATH}")

    existing_names = load_explore_names()
    city_stem_index = build_city_stem_index()

    rows = []
    with VUZOPEDIA_INDEX_PATH.open(encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line:
                rows.append(json.loads(line))

    candidate_rows = [r for r in rows if normalize(r["name"]) not in existing_names]
    new_rows = [r for r in candidate_rows if not SUB_ENTITY_RE.search(r["name"])]
    excluded_sub_entities = len(candidate_rows) - len(new_rows)
    print(
        f"Всего в индексе: {len(rows)}, новых (не в explore/*.md): {len(candidate_rows)}, "
        f"из них факультеты/кафедры (исключены): {excluded_sub_entities}, "
        f"к записи: {len(new_rows)}"
    )

    if args.limit:
        new_rows = new_rows[: args.limit]

    existing_numbers = [
        int(m.group(1)) for p in EXPLORE_DIR.glob("*.md") if (m := re.match(r"(\d+)-", p.name))
    ]
    next_num = max(existing_numbers, default=0) + 1

    used_slugs: set[str] = set()
    city_found = 0
    written = 0

    for row in new_rows:
        name = row["name"]
        vid = row["id"]

        city_region = extract_city(name, city_stem_index)
        if city_region:
            city, region = city_region
            city_found += 1
            address_line = f"г. {city}"
        else:
            city, region = None, None
            address_line = "информация требует уточнения"

        base_slug = slugify(name, vid)
        slug = base_slug
        i = 2
        while slug in used_slugs:
            slug = f"{base_slug}-{i}"
            i += 1
        used_slugs.add(slug)

        filename = f"{next_num}-{slug}.md"
        next_num += 1

        city_field = city if city else "информация требует уточнения"

        lines = [
            f"# {name}",
            "",
            f"- **Полное название:** {name}",
            "- **Сокращённое название:** информация требует уточнения",
            f"- **Город:** {city_field}",
            "- **Год основания:** информация требует уточнения",
            "- **Тип:** информация требует уточнения",
            "- **Ректор:** информация требует уточнения",
            f"- **Адрес:** {address_line}",
            "- **Сайт:** информация требует уточнения",
            "- **Число студентов:** информация требует уточнения",
            "",
            "## Источники",
            f"- https://vuzopedia.ru/vuz/{vid}",
        ]
        (EXPLORE_DIR / filename).write_text("\n".join(lines) + "\n", encoding="utf-8")
        written += 1

    print(f"Записано файлов: {written}, из них с определённым городом: {city_found}")


if __name__ == "__main__":
    main()
