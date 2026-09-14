#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Для каждого вуза, сматченного с ucheba.ru (scripts/university_aggregator_mapping.json,
см. match_universities_to_ucheba.py), забирает страницу(ы) факультетов/институтов
(https://www.ucheba.ru/uz/{id}/units, с пагинацией ?page=2,3,...) и пишет результат в
explore/faculties/<тот же slug>.md -- по аналогии с исходным explore/*.md: заголовок,
список факультетов/институтов, секция "Источники". Прямые HTTP-запросы по уже известным
URL, без единого обращения к веб-поиску.

Как извлекается список: на странице каждое название подразделения оканчивается полным
или кратким названием вуза (например, "Физический факультет МГУ имени М.В. Ломоносова") --
скрипт срезает этот суффикс, используя названия из индекса сопоставления. Пагинация
останавливается, когда очередная страница не добавляет новых названий, либо когда собрано
заявленное на странице число подразделений ("N подразделений"), либо после MAX_PAGES.

Где извлечь не удалось или подразделений оказалось меньше заявленного -- в конец списка
добавляется явная пометка "требует уточнения", как и в остальном пайплайне issue #73:
не выдумываем то, что не смогли собрать.

Запуск:
    py scripts/fetch_faculties_ucheba.py               # все сматченные вузы
    py scripts/fetch_faculties_ucheba.py --only 01-msu 06-bmstu   # только перечисленные
    py scripts/fetch_faculties_ucheba.py --force       # перезаписать уже собранные файлы
"""

import argparse
import html
import http.client
import json
import re
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).parent.parent
MAPPING_PATH = Path(__file__).parent / "university_aggregator_mapping.json"
OUT_DIR = ROOT / "explore" / "faculties"

USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
TIMEOUT = 15
MAX_PAGES = 6
TOTAL_RE = re.compile(r"^(\d+)\s+подразделени\w*")
UNIT_HEAD_RE = re.compile(r"^(Факультет|Институт|Школа|Высшая школа|Филиал)\b")


def fetch_page(uid: str, page: int) -> str | None:
    url = f"https://www.ucheba.ru/uz/{uid}/units"
    if page > 1:
        url += f"?page={page}"
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(2):
        try:
            with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
                return resp.read().decode("utf-8", errors="replace")
        except (urllib.error.URLError, TimeoutError, ConnectionError, http.client.HTTPException):
            if attempt == 0:
                time.sleep(1)
                continue
            return None
    return None


def html_to_lines(page_html: str) -> list[str]:
    body = re.sub(r"<(script|style)[^>]*>.*?</\1>", "", page_html, flags=re.S)
    text = re.sub(r"<[^>]+>", "\n", body)
    text = html.unescape(text)
    return [l.strip() for l in text.split("\n") if l.strip()]


STEM_LEN = 5
STEM_SEARCH_WINDOW = 70  # суффикс-название ищем только в хвосте строки такой длины


def word_stems(name: str) -> list[str]:
    """Первые STEM_LEN символов каждого значимого слова -- склонение (падеж) обычно меняет
    только окончание, поэтому короткий префикс слова устойчив к падежам ("Финансов" из
    "Финансовый"/"Финансового", "Университет" из "университет"/"университета" и т.д.)."""
    words = [w for w in re.split(r"[\s\-]+", name) if len(w) > 2]
    return [w[:STEM_LEN].lower() for w in words]


def build_suffixes(short: str, full: str) -> list[list[str]]:
    """Возвращает список последовательностей стеммированных слов (короткое/полное название),
    длиннейшая последовательность -- первой."""
    variants = []
    for name in (full, short):
        if name:
            stems = word_stems(name)
            if stems:
                variants.append(stems)
    variants.sort(key=len, reverse=True)
    return variants


def extract_units(lines: list[str], suffix_stem_variants: list[list[str]]) -> tuple[list[str], int | None]:
    """suffix_stem_variants -- см. build_suffixes(). Название подразделения на странице обычно
    оканчивается названием вуза в родительном или именительном падеже ("Факультет X
    Финансового университета" / "Факультет X МГУ имени...") -- падеж может отличаться от
    того, что в <title>, поэтому ищем не точный суффикс, а совпадение по стеммированным
    словам в хвосте строки."""
    names: list[str] = []
    total: int | None = None
    for line in lines:
        m = TOTAL_RE.match(line)
        if m:
            total = int(m.group(1))
            continue
        if len(line) > 100 or not UNIT_HEAD_RE.match(line):
            continue

        tail_start = max(0, len(line) - STEM_SEARCH_WINDOW)
        lower = line.lower()
        cut_pos = None
        for stems in suffix_stem_variants:
            if not stems:
                continue
            first_stem = stems[0]
            idx = lower.rfind(first_stem)
            if idx <= 0 or idx < tail_start:
                continue
            # проверяем, что и остальные слова названия вуза (если есть) действительно
            # присутствуют дальше по строке -- защита от случайного совпадения первого слова
            rest_ok = True
            search_from = idx + len(first_stem)
            for stem in stems[1:]:
                pos = lower.find(stem, search_from)
                if pos == -1:
                    rest_ok = False
                    break
                search_from = pos + len(stem)
            if rest_ok and (cut_pos is None or idx < cut_pos):
                cut_pos = idx
        if cut_pos:
            clean = line[:cut_pos].strip(" -—")
            if clean and clean not in names:
                names.append(clean)
    return names, total


def fetch_faculties_for(uid: str, short: str, full: str) -> tuple[list[str], int | None]:
    suffixes = build_suffixes(short, full)
    collected: list[str] = []
    stated_total: int | None = None
    for page in range(1, MAX_PAGES + 1):
        page_html = fetch_page(uid, page)
        if page_html is None:
            break
        lines = html_to_lines(page_html)
        names, total = extract_units(lines, suffixes)
        if total is not None:
            stated_total = total
        new = [n for n in names if n not in collected]
        if not new:
            break
        collected.extend(new)
        if stated_total is not None and len(collected) >= stated_total:
            break
        time.sleep(0.3)
    return collected, stated_total


def write_explore_file(slug: str, display_name: str, source_url: str, units: list[str], stated_total: int | None) -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    lines = [f"# {display_name} — факультеты/институты", "", "## Факультеты/институты"]
    for u in units:
        lines.append(f"- {u}")
    if stated_total is not None and len(units) < stated_total:
        lines.append(
            f"- (информация требует уточнения — источник указывает {stated_total} подразделений "
            f"всего, собрано {len(units)}; остальные не удалось извлечь автоматически)"
        )
    elif not units:
        lines.append("- информация требует уточнения")
    lines += ["", "## Источники", f"- {source_url}"]
    (OUT_DIR / f"{slug}.md").write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--only", nargs="*", default=None, help="Список slug'ов (имя файла без .md)")
    parser.add_argument("--force", action="store_true", help="Перезаписать уже существующие explore/faculties/*.md")
    parser.add_argument(
        "--include-fuzzy",
        action="store_true",
        help=(
            "Включить нечёткие совпадения (confidence=fuzzy), а не только точные. ОСТОРОЖНО: "
            "нечёткое сопоставление показало систематические ложные совпадения на сетях филиалов "
            "(РАНХиГС/РЭУ/ФСБ -- разные региональные филиалы схлопывались в один и тот же вуз "
            "источника из-за почти идентичных названий, отличающихся только городом). Без ручной "
            "проверки конкретных fuzzy-записей использовать не рекомендуется."
        ),
    )
    args = parser.parse_args()

    if not MAPPING_PATH.exists():
        raise SystemExit(f"Не найден {MAPPING_PATH} -- сначала запустите match_universities_to_ucheba.py")

    mapping = json.loads(MAPPING_PATH.read_text(encoding="utf-8"))
    if not args.include_fuzzy:
        skipped_fuzzy = sum(1 for v in mapping.values() if v.get("confidence") == "fuzzy")
        mapping = {k: v for k, v in mapping.items() if v.get("confidence") != "fuzzy"}
        if skipped_fuzzy:
            print(f"Пропущено {skipped_fuzzy} нечётких совпадений (см. --include-fuzzy)", file=sys.stderr)

    slugs = args.only if args.only else list(mapping.keys())
    done = skipped = failed = 0

    for slug in slugs:
        entry = mapping.get(slug)
        if not entry or not entry.get("ucheba_id"):
            continue

        out_path = OUT_DIR / f"{slug}.md"
        if out_path.exists() and not args.force:
            skipped += 1
            continue

        uid = entry["ucheba_id"]
        title = entry["matched_title"] or ""
        short, _, full = title.partition(",")
        short, full = short.strip(), full.strip() or short.strip()

        units, stated_total = fetch_faculties_for(uid, short, full)
        source_url = f"https://www.ucheba.ru/uz/{uid}/units"
        write_explore_file(slug, full or short, source_url, units, stated_total)

        if units:
            done += 1
        else:
            failed += 1
        print(f"{slug}: {len(units)}/{stated_total if stated_total is not None else '?'} -> {out_path.name}")

    print(f"Готово: {done} собрано, {skipped} пропущено (уже есть), {failed} без данных")


if __name__ == "__main__":
    main()
