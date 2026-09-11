#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Строит офлайн-индекс "ucheba.ru ID вуза -> название" по sitemap ucheba.ru, чтобы потом
сматчить его с уже известными 700 вузами из explore/*.md (см. match_universities_to_ucheba.py)
без единого запроса к веб-поиску -- только прямые HTTP-запросы по уже известным URL.

Как это работает:
  1. Забирает https://www.ucheba.ru/sitemaps/www/uz.xml -- список всех /uz/{id} страниц
     сайта (~5000 вузов/колледжей, не только те, что нужны нам).
  2. Для каждого ID делает лёгкий partial-запрос (Range: bytes=0-4095) -- в первых ~4 КБ
     HTML уже есть <title>ID</title> вида "Краткое название, Полное название", этого
     достаточно для сопоставления, полную страницу (обычно 250-350 КБ) грузить не нужно.
  3. Распараллеливает запросы через ThreadPoolExecutor (сеть -- узкое место, не CPU).
  4. Пишет результат построчно в JSON Lines (scripts/ucheba_id_index.jsonl) по мере
     получения -- чтобы промежуточный прогресс не терялся при прерывании, и чтобы можно
     было безопасно перезапускать (--force игнорирует уже собранные ID, без --force скрипт
     докачивает только то, чего ещё нет в файле).

Запуск:
    py scripts/build_ucheba_id_index.py
    py scripts/build_ucheba_id_index.py --force   # перекачать всё заново
    py scripts/build_ucheba_id_index.py --limit 50  # для быстрой проверки на подмножестве
"""

import argparse
import json
import re
import sys
import urllib.error
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from pathlib import Path

SITEMAP_URL = "https://www.ucheba.ru/sitemaps/www/uz.xml"
UZ_URL_RE = re.compile(r"<loc>https://www\.ucheba\.ru/uz/(\d+)</loc>")
TITLE_RE = re.compile(r"<title>(.*?)</title>", re.S)

OUT_PATH = Path(__file__).parent / "ucheba_id_index.jsonl"

USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
CONCURRENCY = 16
TIMEOUT = 12
RANGE_BYTES = "bytes=0-4095"


def fetch_sitemap_ids() -> list[str]:
    req = urllib.request.Request(SITEMAP_URL, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(req, timeout=30) as resp:
        xml = resp.read().decode("utf-8", errors="replace")
    return UZ_URL_RE.findall(xml)


def fetch_title(uid: str) -> tuple[str, str | None]:
    url = f"https://www.ucheba.ru/uz/{uid}"
    req = urllib.request.Request(
        url,
        headers={"User-Agent": USER_AGENT, "Range": RANGE_BYTES, "Accept": "text/html"},
    )
    try:
        with urllib.request.urlopen(req, timeout=TIMEOUT) as resp:
            chunk = resp.read(8192)
    except (urllib.error.URLError, TimeoutError, ConnectionError):
        return uid, None

    text = chunk.decode("utf-8", errors="ignore")
    m = TITLE_RE.search(text)
    if not m:
        return uid, None
    title = re.sub(r"\s+", " ", m.group(1)).strip()
    return uid, title or None


def split_title(title: str) -> tuple[str, str]:
    """Название на ucheba.ru обычно вида "Краткое, Полное" -- делим по первой запятой."""
    if "," in title:
        short, full = title.split(",", 1)
        return short.strip(), full.strip()
    return title, title


def load_existing_ids(path: Path) -> set[str]:
    if not path.exists():
        return set()
    ids = set()
    with path.open(encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            ids.add(json.loads(line)["id"])
    return ids


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true", help="Перекачать все ID заново")
    parser.add_argument("--limit", type=int, default=None, help="Ограничить число ID (для теста)")
    args = parser.parse_args()

    print("Забираю sitemap...")
    ids = fetch_sitemap_ids()
    print(f"Всего ID в sitemap: {len(ids)}")

    if args.limit:
        ids = ids[: args.limit]

    if args.force and OUT_PATH.exists():
        OUT_PATH.unlink()

    already = load_existing_ids(OUT_PATH)
    todo = [uid for uid in ids if uid not in already]
    print(f"Уже собрано: {len(already)}, осталось: {len(todo)}")

    if not todo:
        print("Нечего собирать -- используйте --force для полной пересборки.")
        return

    done = 0
    errors = 0
    with OUT_PATH.open("a", encoding="utf-8") as out, ThreadPoolExecutor(max_workers=CONCURRENCY) as pool:
        futures = {pool.submit(fetch_title, uid): uid for uid in todo}
        for fut in as_completed(futures):
            uid, title = fut.result()
            done += 1
            if title is None:
                errors += 1
                out.write(json.dumps({"id": uid, "title": None, "short": None, "full": None}, ensure_ascii=False) + "\n")
            else:
                short, full = split_title(title)
                out.write(json.dumps({"id": uid, "title": title, "short": short, "full": full}, ensure_ascii=False) + "\n")
            if done % 200 == 0:
                out.flush()
                print(f"  {done}/{len(todo)} (ошибок: {errors})", file=sys.stderr)

    print(f"Готово: {done} обработано, {errors} ошибок. Результат -> {OUT_PATH}")


if __name__ == "__main__":
    main()
