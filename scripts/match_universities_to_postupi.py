#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Второй проход сопоставления -- те вузы из explore/*.md, которых не удалось безопасно
сматчить с ucheba.ru (scripts/university_aggregator_mapping.json, confidence != "exact"),
пробуем найти на postupi.online (scripts/postupi_id_index.jsonl, см.
build_postupi_id_index.py). У postupi.online, в отличие от ucheba.ru, есть отдельные страницы
на региональные филиалы сетей вроде РАНХиГС/ЮФУ -- у ucheba.ru таких страниц просто нет
(проверено вручную), так что это не повтор той же ошибки, а действительно другой охват.

Правила сопоставления -- те же защиты от ложных совпадений, что и в
match_universities_to_ucheba.py:
  - берём только ТОЧНЫЕ совпадения по нормализованному названию;
  - краткое название используется только если оно однозначно -- и на стороне индекса
    postupi.online (несколько разных URL с одинаковым кратким именем -- исключаем), и на
    стороне explore/*.md (несколько разных наших вузов с одинаковым кратким именем --
    исключаем, см. ту же логику с "МГПУ").
  - fuzzy здесь не делаем вовсе -- ложные совпадения на сетях филиалов уже показали, что
    редакционные вариации в названиях слишком похожи, а второй источник данных для того и
    нужен, чтобы расширить точные совпадения, а не рисковать новыми ложными.

Результат сохраняется в scripts/university_aggregator_mapping.json -- добавляет
"postupi_url"/"postupi_confidence" только тем вузам, у кого ucheba confidence != "exact"
(существующие точные совпадения ucheba.ru не трогает).

Запуск:
    py scripts/match_universities_to_postupi.py
"""

import json
import re
from pathlib import Path

EXPLORE_DIR = Path(__file__).parent.parent / "explore"
POSTUPI_INDEX_PATH = Path(__file__).parent / "postupi_id_index.jsonl"
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


def slug_from_url(url: str) -> str:
    return url.rstrip("/").rsplit("/", 1)[-1]


def main() -> None:
    if not POSTUPI_INDEX_PATH.exists():
        raise SystemExit(f"Нет {POSTUPI_INDEX_PATH} -- сначала build_postupi_id_index.py")
    if not MAPPING_PATH.exists():
        raise SystemExit(f"Нет {MAPPING_PATH} -- сначала match_universities_to_ucheba.py")

    mapping = json.loads(MAPPING_PATH.read_text(encoding="utf-8"))

    index_rows = []
    with POSTUPI_INDEX_PATH.open(encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if line:
                index_rows.append(json.loads(line))
    print(f"Записей в индексе postupi.online: {len(index_rows)}")

    # Для короткого названия используем сам slug вуза в URL как приближение "короткого
    # имени" (у postupi.online нет отдельного поля short) -- он часто и есть аббревиатура
    # (urfu-im-b-n-elcina, kemgu, mgpu). Полное название -- поле name со страницы каталога.
    by_full: dict[str, str] = {}
    full_name_id_sets: dict[str, set[str]] = {}
    by_slug: dict[str, str] = {}
    slug_id_sets: dict[str, set[str]] = {}
    for row in index_rows:
        url = row["url"]
        norm_full = normalize(row["name"])
        if norm_full:
            full_name_id_sets.setdefault(norm_full, set()).add(url)
            by_full.setdefault(norm_full, url)
        norm_slug = normalize(slug_from_url(url).replace("-", " "))
        if norm_slug:
            slug_id_sets.setdefault(norm_slug, set()).add(url)
            by_slug.setdefault(norm_slug, url)

    ambiguous_full_on_postupi = {n for n, ids in full_name_id_sets.items() if len(ids) > 1}
    for n in ambiguous_full_on_postupi:
        del by_full[n]
    ambiguous_slug_on_postupi = {n for n, ids in slug_id_sets.items() if len(ids) > 1}
    for n in ambiguous_slug_on_postupi:
        del by_slug[n]

    explore_files = sorted(EXPLORE_DIR.glob("*.md"))
    all_fields = {p.stem: parse_explore_file(p) for p in explore_files}

    short_to_full_names: dict[str, set[str]] = {}
    for fields in all_fields.values():
        norm_short = normalize(fields.get("Сокращённое название", ""))
        norm_full = normalize(fields.get("Полное название", ""))
        if norm_short and norm_full:
            short_to_full_names.setdefault(norm_short, set()).add(norm_full)
    ambiguous_short_our_side = {n for n, fulls in short_to_full_names.items() if len(fulls) > 1}

    new_exact = 0
    for slug, fields in all_fields.items():
        entry = mapping.get(slug, {})
        if entry.get("confidence") == "exact":
            continue  # уже надёжно сматчено на ucheba.ru

        full_name = fields.get("Полное название", "")
        short_name = fields.get("Сокращённое название", "")
        norm_full = normalize(full_name)
        norm_short = normalize(short_name)

        url = by_full.get(norm_full)
        if not url and norm_short not in ambiguous_short_our_side:
            url = by_slug.get(norm_short)

        if url:
            mapping.setdefault(slug, {})
            mapping[slug]["postupi_url"] = url
            mapping[slug]["postupi_confidence"] = "exact"
            new_exact += 1

    MAPPING_PATH.write_text(json.dumps(mapping, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Новых точных совпадений через postupi.online: {new_exact}")
    print(f"Результат -> {MAPPING_PATH}")


if __name__ == "__main__":
    main()
