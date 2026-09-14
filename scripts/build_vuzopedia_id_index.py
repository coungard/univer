#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Строит офлайн-индекс "ID вуза на vuzopedia.ru -> название" -- третий источник данных о
факультетах (после ucheba.ru и postupi.online). В отличие от них, у vuzopedia.ru нет XML
sitemap, но robots.txt разрешает `*sitemap?page=`, а сама страница `/sitemap` (без XML,
обычный HTML со сплошным списком ссылок `<a href="/vuz/{id}">Название</a>`) содержит ВСЕ
вузы сразу на одной (большой) странице -- сложный обход по категориям/регионам, как у
postupi.online, не нужен.

Как это работает:
  1. Забирает https://vuzopedia.ru/sitemap -- страница со ссылками на вузы вперемешку с
     другими разделами сайта (доп. образование и т.п.), фильтруем только `/vuz/{id}`.
  2. Проверяет пагинацию (внизу страницы ссылки вида "/sitemap-1", "/sitemap-2", ...) --
     на практике все вузы оказались только на первой странице (остальные -- другие разделы
     сайта, специальности/программы/калькулятор), но на будущее переобходим все страницы.
  3. Пишет результат в JSON Lines (scripts/vuzopedia_id_index.jsonl).

Запуск:
    py scripts/build_vuzopedia_id_index.py
    py scripts/build_vuzopedia_id_index.py --force
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

BASE = "https://vuzopedia.ru"
OUT_PATH = Path(__file__).parent / "vuzopedia_id_index.jsonl"
USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
TIMEOUT = 60
VUZ_LINK_RE = re.compile(r'<a href="/vuz/(\d+)">([^<]*)</a>')
PAGE_LINK_RE = re.compile(r'<a href="(/sitemap(?:-\d+)?)">\d+</a>')


def fetch(url: str) -> str | None:
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    for attempt in range(2):
        try:
            with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
                return resp.read().decode("utf-8", errors="replace")
        except (urllib.error.URLError, TimeoutError, ConnectionError, http.client.HTTPException):
            if attempt == 0:
                time.sleep(2)
                continue
            return None
    return None


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if OUT_PATH.exists() and not args.force:
        print(f"{OUT_PATH} уже существует -- используйте --force", file=sys.stderr)
        return

    print("Забираю /sitemap...", file=sys.stderr)
    first = fetch(f"{BASE}/sitemap")
    if not first:
        raise SystemExit("Не удалось забрать /sitemap")

    page_paths = sorted(set(PAGE_LINK_RE.findall(first)))
    print(f"Найдено страниц пагинации: {page_paths}", file=sys.stderr)

    entries: dict[str, str] = {}
    pages_content = [first]
    for path in page_paths:
        if path == "/sitemap":
            continue
        content = fetch(f"{BASE}{path}")
        if content:
            pages_content.append(content)
        time.sleep(0.5)

    for content in pages_content:
        for m in VUZ_LINK_RE.finditer(content):
            vid, name = m.group(1), html.unescape(m.group(2)).strip()
            entries.setdefault(vid, name)

    with OUT_PATH.open("w", encoding="utf-8") as out:
        for vid, name in sorted(entries.items(), key=lambda x: int(x[0])):
            out.write(json.dumps({"id": vid, "name": name}, ensure_ascii=False) + "\n")

    print(f"Готово: {len(entries)} вузов -> {OUT_PATH}", file=sys.stderr)


if __name__ == "__main__":
    main()
