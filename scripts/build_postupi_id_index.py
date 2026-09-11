#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Строит офлайн-индекс "URL вуза на postupi.online -> название" -- второй источник данных о
факультетах (после ucheba.ru, см. build_ucheba_id_index.py), нужен для вузов, которых нет в
индексе ucheba.ru (в частности -- у postupi.online, в отличие от ucheba.ru, есть отдельные
страницы на региональные филиалы сетей вроде РАНХиГС/ЮФУ, а не только на головной вуз).

Как это работает:
  1. Забирает https://postupi.online/postupi_sitemap/ru/sitemap_vuzy.xml -- список ~85
     страниц-каталогов по специализации ("vuzspec-pedagogical" и т.п., без разбивки по
     регионам -- у postupi.online нет единого sitemap с прямым перечнем вузов).
  2. Для каждой страницы-каталога обходит пагинацию (?page_num=2,3,...), пока очередная
     страница не перестаёт добавлять новые вузы (либо MAX_PAGES).
  3. С каждой страницы вытаскивает пары (URL вуза с региональным поддоменом, название) --
     ссылки вида https://{регион}.postupi.online/vuz/{slug}/.
  4. Пишет результат построчно в JSON Lines (scripts/postupi_id_index.jsonl), дедуплицируя
     по URL -- один вуз может встретиться в нескольких категориях.

Запуск:
    py scripts/build_postupi_id_index.py
    py scripts/build_postupi_id_index.py --force
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
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

SITEMAP_VUZY_URL = "https://postupi.online/postupi_sitemap/ru/sitemap_vuzy.xml"
CATEGORY_RE = re.compile(r"<loc>(https://postupi\.online/vuzi/[a-z0-9\-]+/)</loc>")
VUZ_LINK_RE = re.compile(
    r'<a[^>]+href="(https://[a-z0-9.\-]*postupi\.online/vuz/[a-z0-9\-]+/)"[^>]*>(.*?)</a>', re.S
)

OUT_PATH = Path(__file__).parent / "postupi_id_index.jsonl"
USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
TIMEOUT = 15
CONCURRENCY = 10
MAX_PAGES = 15


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


def fetch_category_ids() -> list[str]:
    xml = fetch(SITEMAP_VUZY_URL)
    if not xml:
        raise SystemExit("Не удалось забрать sitemap_vuzy.xml")
    return CATEGORY_RE.findall(xml)


def extract_vuz_links(page_html: str) -> dict[str, str]:
    result = {}
    for m in VUZ_LINK_RE.finditer(page_html):
        url, text = m.group(1), m.group(2)
        text = re.sub(r"<[^>]+>", "", text)
        text = html.unescape(text).strip()
        if url and text:
            result.setdefault(url, text)
    return result


def crawl_category(category_url: str) -> dict[str, str]:
    collected: dict[str, str] = {}
    for page in range(1, MAX_PAGES + 1):
        url = category_url if page == 1 else f"{category_url}?page_num={page}"
        page_html = fetch(url)
        if not page_html:
            break
        links = extract_vuz_links(page_html)
        new_keys = [k for k in links if k not in collected]
        if not new_keys:
            break
        collected.update(links)
        time.sleep(0.2)
    return collected


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true")
    args = parser.parse_args()

    if OUT_PATH.exists() and not args.force:
        print(f"{OUT_PATH} уже существует -- используйте --force для пересборки", file=sys.stderr)
        return

    print("Забираю список категорий...", file=sys.stderr)
    categories = fetch_category_ids()
    print(f"Категорий: {len(categories)}", file=sys.stderr)

    all_vuz: dict[str, str] = {}
    done = 0
    with ThreadPoolExecutor(max_workers=CONCURRENCY) as pool:
        futures = {pool.submit(crawl_category, cat): cat for cat in categories}
        for fut in as_completed(futures):
            result = fut.result()
            all_vuz.update(result)
            done += 1
            print(f"  {done}/{len(categories)} категорий, всего вузов: {len(all_vuz)}", file=sys.stderr)

    with OUT_PATH.open("w", encoding="utf-8") as out:
        for url, name in sorted(all_vuz.items()):
            out.write(json.dumps({"url": url, "name": name}, ensure_ascii=False) + "\n")

    print(f"Готово: {len(all_vuz)} вузов -> {OUT_PATH}", file=sys.stderr)


if __name__ == "__main__":
    main()
