#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Третий проход сопоставления -- вузы из explore/*.md, не сматченные ни с ucheba.ru, ни с
postupi.online (scripts/university_aggregator_mapping.json), пробуем найти на vuzopedia.ru
(scripts/vuzopedia_id_index.jsonl, см. build_vuzopedia_id_index.py).

Те же защиты от ложных совпадений, что и в предыдущих двух проходах:
  - только точные совпадения по нормализованному названию;
  - короткое название (аббревиатура) используется для сопоставления только если оно
    однозначно -- и на стороне индекса vuzopedia.ru (у него только одно поле "name" на
    запись, короткое отдельно не хранится, поэтому короткое имя не используется как ключ
    поиска на этой стороне вовсе, только полное), и на стороне explore/*.md (см. ту же
    логику с "МГПУ" в предыдущих скриптах).
  - fuzzy здесь не делаем -- третий источник существует именно чтобы расширить точные
    совпадения, а не плодить новые риски ложных срабатываний.

Результат сохраняется в scripts/university_aggregator_mapping.json -- добавляет
"vuzopedia_id" только тем вузам, у кого ещё нет ни ucheba (exact), ни postupi_url.

Запуск:
    py scripts/match_universities_to_vuzopedia.py
"""

import json
import re
from pathlib import Path

EXPLORE_DIR = Path(__file__).parent.parent / "explore"
VUZOPEDIA_INDEX_PATH = Path(__file__).parent / "vuzopedia_id_index.jsonl"
MAPPING_PATH = Path(__file__).parent / "university_aggregator_mapping.json"

FIELD_RE = re.compile(r"^-\s*\*\*([^:*]+):\*\*\s*(.*)$")

STRIP_PREFIXES = [
    "федеральное государственное бюджетное образовательное учреждение высшего образования",
    "федеральное государственное автономное образовательное учреждение высшего образования",
    "федеральное государственное казённое образовательное учреждение высшего образования",
    "государственное образовательное учреждение высшего образования",
    "образовательное учреждение высшего образования",
    "негосударственное образовательное частное учреждение высшего образования",
    "частное образовательное учреждение высшего образования",
    "автономная некоммерческая организация высшего образования",
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


def parse_explore_file(path: Path) -> dict:
    fields = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m:
            fields[m.group(1).strip()] = m.group(2).strip()
    return fields


def already_matched(entry: dict) -> bool:
    return entry.get("confidence") == "exact" or bool(entry.get("postupi_url"))


def main() -> None:
    if not VUZOPEDIA_INDEX_PATH.exists():
        raise SystemExit(f"Нет {VUZOPEDIA_INDEX_PATH} -- сначала build_vuzopedia_id_index.py")
    if not MAPPING_PATH.exists():
        raise SystemExit(f"Нет {MAPPING_PATH}")

    mapping = json.loads(MAPPING_PATH.read_text(encoding="utf-8"))

    index_rows = []
    with VUZOPEDIA_INDEX_PATH.open(encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line:
                index_rows.append(json.loads(line))
    print(f"Записей в индексе vuzopedia.ru: {len(index_rows)}")

    by_full: dict[str, str] = {}
    full_id_sets: dict[str, set[str]] = {}
    for row in index_rows:
        norm = normalize(row["name"])
        if norm:
            full_id_sets.setdefault(norm, set()).add(row["id"])
            by_full.setdefault(norm, row["id"])
    ambiguous_on_vuzopedia = {n for n, ids in full_id_sets.items() if len(ids) > 1}
    for n in ambiguous_on_vuzopedia:
        del by_full[n]
    if ambiguous_on_vuzopedia:
        print(f"Неоднозначных названий в индексе vuzopedia.ru (исключены): {len(ambiguous_on_vuzopedia)}")

    explore_files = sorted(EXPLORE_DIR.glob("*.md"))
    all_fields = {p.stem: parse_explore_file(p) for p in explore_files}

    new_exact = 0
    for slug, fields in all_fields.items():
        entry = mapping.get(slug, {})
        if already_matched(entry):
            continue

        full_name = fields.get("Полное название", "")
        norm_full = normalize(full_name)
        vid = by_full.get(norm_full)

        if vid:
            row = next(r for r in index_rows if r["id"] == vid)
            mapping.setdefault(slug, {})
            mapping[slug]["vuzopedia_id"] = vid
            mapping[slug]["vuzopedia_name"] = row["name"]
            new_exact += 1

    MAPPING_PATH.write_text(json.dumps(mapping, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Новых точных совпадений через vuzopedia.ru: {new_exact}")
    print(f"Результат -> {MAPPING_PATH}")


if __name__ == "__main__":
    main()
