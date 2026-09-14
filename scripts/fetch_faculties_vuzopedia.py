#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Для каждого вуза, сматченного с vuzopedia.ru (scripts/university_aggregator_mapping.json,
поле vuzopedia_id, см. match_universities_to_vuzopedia.py), забирает страницу подразделений
(/vuz/{id}/podrazdeleniya) и пишет/дополняет explore/faculties/<slug>.md -- по тому же
формату, что и fetch_faculties_ucheba.py / fetch_faculties_postupi.py.

Разбор страницы: после заголовка "Факультеты" идёт плоский список названий подряд, каждое
оканчивается коротким или полным названием вуза (например, "Факультет Ивановского филиала
РЭУ им. Г.В. Плеханова") -- суффикс убирается сравнением по стеммам слов (падеж может не
совпадать с тем, что в explore/*.md), как и в fetch_faculties_ucheba.py. Список
заканчивается на строке "Задайте вузу вопрос" (стоп-маркер).

Запуск:
    py scripts/fetch_faculties_vuzopedia.py
    py scripts/fetch_faculties_vuzopedia.py --force
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
EXPLORE_DIR = ROOT / "explore"
OUT_DIR = EXPLORE_DIR / "faculties"
FIELD_RE = re.compile(r"^-\s*\*\*([^:*]+):\*\*\s*(.*)$")

USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
TIMEOUT = 15
STOP_MARKER = "Задайте вузу вопрос"
SECTION_HEADS = ("Факультеты", "Институты", "Кафедры", "Филиалы")
STEM_LEN = 5


def fetch(url: str) -> str | None:
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


def word_stems(name: str) -> list[str]:
    words = [w for w in re.split(r"[\s\-]+", name) if len(w) > 2]
    return [w[:STEM_LEN].lower() for w in words]


def strip_suffix(line: str, stem_variants: list[list[str]]) -> str:
    lower = line.lower()
    tail_start = max(0, len(line) - 70)
    cut_pos = None
    for stems in stem_variants:
        if not stems:
            continue
        idx = lower.rfind(stems[0])
        if idx <= 0 or idx < tail_start:
            continue
        search_from = idx + len(stems[0])
        ok = True
        for stem in stems[1:]:
            pos = lower.find(stem, search_from)
            if pos == -1:
                ok = False
                break
            search_from = pos + len(stem)
        if ok and (cut_pos is None or idx < cut_pos):
            cut_pos = idx
    if cut_pos:
        return line[:cut_pos].strip(" -—")
    return line


def extract_units(lines: list[str], stem_variants: list[list[str]]) -> list[str]:
    names: list[str] = []
    in_section = False
    for line in lines:
        if line in SECTION_HEADS:
            in_section = True
            continue
        if line == STOP_MARKER:
            break
        if not in_section:
            continue
        if len(line) > 120 or line.isdigit():
            continue
        clean = strip_suffix(line, stem_variants)
        if clean and clean != line[:0] and clean not in names and len(clean) > 2:
            names.append(clean)
    return names


def explore_fields(slug: str) -> dict:
    path = EXPLORE_DIR / f"{slug}.md"
    fields = {}
    if not path.exists():
        return fields
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m:
            fields[m.group(1).strip()] = m.group(2).strip()
    return fields


def write_file(slug: str, display_name: str, source_url: str, units: list[str]) -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    lines = [f"# {display_name} — факультеты/институты", "", "## Факультеты/институты"]
    for u in units:
        lines.append(f"- {u}")
    if not units:
        lines.append("- информация требует уточнения")
    lines += ["", "## Источники", f"- {source_url}"]
    (OUT_DIR / f"{slug}.md").write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--only", nargs="*", default=None)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if not MAPPING_PATH.exists():
        raise SystemExit(f"Нет {MAPPING_PATH}")
    mapping = json.loads(MAPPING_PATH.read_text(encoding="utf-8"))

    slugs = args.only if args.only else list(mapping.keys())
    done = skipped = failed = 0

    for slug in slugs:
        entry = mapping.get(slug)
        if not entry or not entry.get("vuzopedia_id"):
            continue

        out_path = OUT_DIR / f"{slug}.md"
        if out_path.exists() and not args.force:
            skipped += 1
            continue

        vid = entry["vuzopedia_id"]
        url = f"https://vuzopedia.ru/vuz/{vid}/podrazdeleniya"
        page_html = fetch(url)
        if not page_html:
            failed += 1
            print(f"{slug}: сеть не ответила -> {url}")
            continue

        fields = explore_fields(slug)
        short_name = fields.get("Сокращённое название", "")
        full_name = fields.get("Полное название", "") or entry.get("vuzopedia_name", "")
        stem_variants = sorted(
            (word_stems(n) for n in (full_name, short_name) if n), key=len, reverse=True
        )

        lines = html_to_lines(page_html)
        units = extract_units(lines, stem_variants)
        write_file(slug, full_name or short_name or slug, url, units)

        if units:
            done += 1
        else:
            failed += 1
        print(f"{slug}: {len(units)} -> {out_path.name}")

    print(f"Готово: {done} собрано, {skipped} пропущено (уже есть), {failed} без данных")


if __name__ == "__main__":
    main()
