#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Для каждого вуза, сматченного с postupi.online (scripts/university_aggregator_mapping.json,
поле postupi_url, см. match_universities_to_postupi.py), забирает страницу подразделений
(<postupi_url>podrazdeleniya/) и пишет/дополняет explore/faculties/<slug>.md -- по тому же
формату, что и fetch_faculties_ucheba.py. Не трогает вузы, у кого уже есть непустой файл
explore/faculties/*.md (полученный с ucheba.ru) -- используется только для тех, у кого его
ещё нет, либо кто был собран пустым и не был из fetch_faculties_ucheba.py "требует уточнения".

Разбор страницы: заголовок-тип подразделения ("Институт"/"Факультет"/"Школа"/"Филиал")
стоит отдельной строкой, а настоящее название -- через несколько строк ниже, начинается с
того же слова-типа (например "Институт" -> "Институт педагогики"). Смотрим вперёд до
MAX_LOOKAHEAD строк, а не на фиксированное смещение -- на разных вузах разметка страницы
слегка отличается по числу промежуточных строк (город, статус и т.д.).

Запуск:
    py scripts/fetch_faculties_postupi.py
    py scripts/fetch_faculties_postupi.py --only 282-mordgpu
    py scripts/fetch_faculties_postupi.py --force   # перезаписать уже существующие файлы
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


def explore_full_name(slug: str) -> str:
    path = EXPLORE_DIR / f"{slug}.md"
    if not path.exists():
        return slug
    for line in path.read_text(encoding="utf-8").splitlines():
        m = FIELD_RE.match(line.strip())
        if m and m.group(1).strip() == "Полное название":
            return m.group(2).strip()
    return slug

USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
TIMEOUT = 15
MAX_LOOKAHEAD = 6
HEAD_TYPES = ("Институт", "Факультет", "Школа", "Филиал")


def fetch(url: str) -> str | None:
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(2):
        try:
            with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
                raw = resp.read()
        except (urllib.error.URLError, TimeoutError, ConnectionError, http.client.HTTPException):
            if attempt == 0:
                time.sleep(1)
                continue
            return None
        for enc in ("utf-8", "cp1251"):
            try:
                return raw.decode(enc)
            except UnicodeDecodeError:
                continue
        return raw.decode("utf-8", errors="replace")
    return None


def html_to_lines(page_html: str) -> list[str]:
    body = re.sub(r"<(script|style)[^>]*>.*?</\1>", "", page_html, flags=re.S)
    text = re.sub(r"<[^>]+>", "\n", body)
    text = html.unescape(text)
    return [l.strip() for l in text.split("\n") if l.strip()]


def extract_units(lines: list[str]) -> list[str]:
    names: list[str] = []
    n = len(lines)
    for i in range(n):
        head = lines[i]
        if head not in HEAD_TYPES:
            continue
        for j in range(i + 1, min(i + 1 + MAX_LOOKAHEAD, n)):
            cand = lines[j]
            if cand.startswith(head) and len(cand) < 150:
                if cand not in names:
                    names.append(cand)
                break
    return names


def write_or_merge(slug: str, display_name: str, source_url: str, units: list[str]) -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    path = OUT_DIR / f"{slug}.md"
    lines = [f"# {display_name} — факультеты/институты", "", "## Факультеты/институты"]
    for u in units:
        lines.append(f"- {u}")
    if not units:
        lines.append("- информация требует уточнения")
    lines += ["", "## Источники", f"- {source_url}"]
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


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
        if not entry or not entry.get("postupi_url"):
            continue

        out_path = OUT_DIR / f"{slug}.md"
        if out_path.exists() and not args.force:
            skipped += 1
            continue

        url = entry["postupi_url"].rstrip("/") + "/podrazdeleniya/"
        page_html = fetch(url)
        if not page_html:
            failed += 1
            print(f"{slug}: сеть не ответила -> {url}")
            continue

        lines = html_to_lines(page_html)
        units = extract_units(lines)
        display_name = explore_full_name(slug)
        write_or_merge(slug, display_name, url, units)

        if units:
            done += 1
        else:
            failed += 1
        print(f"{slug}: {len(units)} -> {out_path.name}")

    print(f"Готово: {done} собрано, {skipped} пропущено (уже есть), {failed} без данных")


if __name__ == "__main__":
    main()
