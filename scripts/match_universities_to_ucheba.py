#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Сопоставляет 700 вузов из explore/*.md с их ID на ucheba.ru, используя офлайн-индекс
"ID -> название" из build_ucheba_id_index.py (scripts/ucheba_id_index.jsonl). Без единого
запроса к веб-поиску -- чистое локальное сопоставление строк.

Как матчит:
  1. Нормализует названия (нижний регистр, без кавычек/скобок/двойных пробелов, без частых
     префиксов вроде "федеральное государственное бюджетное образовательное учреждение
     высшего образования").
  2. Сперва пытается точное совпадение по нормализованному краткому или полному названию.
  3. Для того, что не совпало точно -- fuzzy-match (difflib) полного названия вуза против
     полных названий из индекса, порог совпадения задаётся --threshold (по умолчанию 0.72).
  4. Пишет результат в scripts/university_aggregator_mapping.json: по каждому вузу (ключ --
     имя файла explore/*.md без .md) -- {"ucheba_id": ..., "matched_title": ..., "score": ...,
     "confidence": "exact"|"fuzzy"} или {"ucheba_id": null, ...} если совпадения не нашлось
     (не выдумываем сопоставление при низкой уверенности -- как и остальной пайплайн issue #73).

Запуск:
    py scripts/match_universities_to_ucheba.py
    py scripts/match_universities_to_ucheba.py --threshold 0.8
"""

import argparse
import difflib
import json
import re
from pathlib import Path

EXPLORE_DIR = Path(__file__).parent.parent / "explore"
INDEX_PATH = Path(__file__).parent / "ucheba_id_index.jsonl"
OUT_PATH = Path(__file__).parent / "university_aggregator_mapping.json"

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
    n = re.sub(r"\s+", " ", n).strip()
    n = n.rstrip(".,")
    return n


def parse_explore_file(path: Path) -> dict:
    fields = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m:
            fields[m.group(1).strip()] = m.group(2).strip()
    return fields


def load_index() -> list[dict]:
    entries = []
    with INDEX_PATH.open(encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            row = json.loads(line)
            if row.get("full"):
                entries.append(row)
    return entries


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--threshold", type=float, default=0.72)
    args = parser.parse_args()

    if not INDEX_PATH.exists():
        raise SystemExit(f"Не найден {INDEX_PATH} -- сначала запустите build_ucheba_id_index.py")

    print("Загружаю индекс ucheba.ru...")
    index = load_index()
    print(f"Записей в индексе: {len(index)}")

    # Индекс для точного совпадения по нормализованному названию (краткому и полному).
    # ВАЖНО: аббревиатуры вроде "МГПУ" не уникальны -- Московский и Мордовский пед.
    # университеты обе сокращаются как МГПУ. Неоднозначность бывает по двум разным осям,
    # и обе надо проверять отдельно:
    #  1. на стороне ucheba.ru -- разные записи индекса с разными id совпали по имени
    #     (name_id_sets ниже);
    #  2. на стороне explore/*.md -- разные вузы из НАШИХ 700 используют одно и то же
    #     краткое название (short_to_full_names ниже) -- именно этот случай и есть баг с
    #     МГПУ: у ucheba.ru запись одна (Московский), но подставить её под оба наших вуза
    #     («122-mgpu» и «282-mordgpu») было бы неверно для второго.
    exact_by_name: dict[str, dict] = {}
    name_id_sets: dict[str, set[str]] = {}
    for row in index:
        for key in ("short", "full"):
            norm = normalize(row[key])
            if not norm:
                continue
            name_id_sets.setdefault(norm, set()).add(row["id"])
            exact_by_name.setdefault(norm, row)
    ambiguous_on_ucheba = {norm for norm, ids in name_id_sets.items() if len(ids) > 1}
    for norm in ambiguous_on_ucheba:
        del exact_by_name[norm]
    if ambiguous_on_ucheba:
        print(f"Неоднозначных названий в индексе ucheba.ru (исключены): {len(ambiguous_on_ucheba)}")

    # Список нормализованных полных названий для fuzzy-поиска
    fuzzy_pool = [(normalize(row["full"]), row) for row in index]
    fuzzy_names = [p[0] for p in fuzzy_pool]

    explore_files = sorted(EXPLORE_DIR.glob("*.md"))
    print(f"Вузов в explore/*.md: {len(explore_files)}")

    all_fields = {p.stem: parse_explore_file(p) for p in explore_files}
    short_to_full_names: dict[str, set[str]] = {}
    for fields in all_fields.values():
        norm_short = normalize(fields.get("Сокращённое название", ""))
        norm_full = normalize(fields.get("Полное название", ""))
        if norm_short and norm_full:
            short_to_full_names.setdefault(norm_short, set()).add(norm_full)
    ambiguous_on_our_side = {norm for norm, fulls in short_to_full_names.items() if len(fulls) > 1}
    if ambiguous_on_our_side:
        print(
            f"Кратких названий, которыми пользуются несколько РАЗНЫХ наших вузов "
            f"(нельзя матчить по краткому имени): {len(ambiguous_on_our_side)} -- "
            f"{', '.join(sorted(ambiguous_on_our_side)[:10])}"
        )

    result = {}
    exact_count = 0
    fuzzy_count = 0
    none_count = 0

    for path in explore_files:
        slug = path.stem
        fields = all_fields[slug]
        full_name = fields.get("Полное название", "")
        short_name = fields.get("Сокращённое название", "")

        norm_full = normalize(full_name)
        norm_short = normalize(short_name)

        match = exact_by_name.get(norm_full)
        if not match and norm_short not in ambiguous_on_our_side:
            match = exact_by_name.get(norm_short)
        if match:
            result[slug] = {
                "ucheba_id": match["id"],
                "matched_title": match["title"],
                "score": 1.0,
                "confidence": "exact",
            }
            exact_count += 1
            continue

        best_score = 0.0
        best_row = None
        if norm_full:
            close = difflib.get_close_matches(norm_full, fuzzy_names, n=1, cutoff=args.threshold)
            if close:
                best_name = close[0]
                idx = fuzzy_names.index(best_name)
                best_row = fuzzy_pool[idx][1]
                best_score = difflib.SequenceMatcher(None, norm_full, best_name).ratio()

        if best_row:
            result[slug] = {
                "ucheba_id": best_row["id"],
                "matched_title": best_row["title"],
                "score": round(best_score, 3),
                "confidence": "fuzzy",
            }
            fuzzy_count += 1
        else:
            result[slug] = {"ucheba_id": None, "matched_title": None, "score": 0.0, "confidence": "none"}
            none_count += 1

    OUT_PATH.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"Точных совпадений: {exact_count}, нечётких: {fuzzy_count}, не найдено: {none_count}")
    print(f"Результат -> {OUT_PATH}")


if __name__ == "__main__":
    main()
