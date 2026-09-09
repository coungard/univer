#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Генерирует вторую data-миграцию Flyway (V16), добавляющую в public.address /
public.universities оставшиеся вузы из explore/*.md, для которых в момент
генерации V14 (см. generate_universities_migration.py) не было проверенного
сайта -- а значит, и email по шаблону info@<домен> вывести было не из чего.

После того как address.email в схеме стал NULLABLE (см. миграцию
V15__make_address_email_nullable.sql), выдумывать эти вузы больше не нужно
пропускать: строка адреса просто сохраняется с email = NULL (и website =
NULL, если сайт неизвестен) вместо того, чтобы либо пропускать вуз, либо
подставлять непроверенный email.

Переиспользует справочник городов (CITY_REGION) и парсер explore/*.md
(parse_file) из generate_universities_migration.py -- см. его же для
описания источника данных и допущений; ничего в самом V14 (и, значит, в уже
однажды сгенерированном UUID для тех 485 вузов) этот скрипт не меняет.

Запуск: py scripts/generate_remaining_universities_migration.py
"""
import sys
import uuid
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import generate_universities_migration as base  # noqa: E402


def main():
    files = sorted(base.EXPLORE_DIR.glob("*.md"))
    records = []
    errors = []
    for path in files:
        try:
            records.append(base.parse_file(path))
        except ValueError as exc:
            errors.append(str(exc))

    if errors:
        print("Ошибки парсинга:", file=sys.stderr)
        for e in errors:
            print(f"  {e}", file=sys.stderr)
        sys.exit(1)

    # Те, у кого email уже выведен (сайт был известен) -- уже в V14, здесь не трогаем.
    remaining = [r for r in records if not r["email"]]

    print(f"Всего файлов: {len(records)}")
    print(f"Уже в V14 (пропускаем здесь): {len(records) - len(remaining)}")
    print(f"Добавляется в V16 (сайт/email неизвестны -> email/website NULL): {len(remaining)}")

    out_dir = base.ROOT / "src" / "main" / "resources" / "db" / "migration" / "data"
    out_path = out_dir / "V16__insert_universities_data_rf_remaining.sql"
    if out_path.exists() and "--force" not in sys.argv:
        print(
            f"\n{out_path.relative_to(base.ROOT)} уже существует и, скорее всего, уже закоммичен и "
            "применён -- см. CLAUDE.md про редактирование применённых миграций. Ничего не записано. "
            "Запустите с флагом --force, если это осознанный пересбор ещё не применённой миграции.",
            file=sys.stderr,
        )
        sys.exit(1)

    lines = [
        "-- Добавление оставшихся вузов России (см. issue #73) в public.address / public.universities --",
        "-- продолжение V14__insert_universities_data_rf.sql для тех, у кого в explore/*.md на момент",
        "-- генерации V14 не было проверенного сайта вуза (и, соответственно, взять email было неоткуда).",
        "-- Стало возможным после V15__make_address_email_nullable.sql: адрес сохраняется с email = NULL",
        "-- (и, где сайт неизвестен, website = NULL) вместо того, чтобы пропускать такой вуз.",
        "-- Источник данных, справочник регионов и другие допущения -- см.",
        "-- scripts/generate_universities_migration.py и scripts/README.md.",
        "",
    ]
    for r in remaining:
        addr_id = str(uuid.uuid4())
        uni_id = str(uuid.uuid4())
        lines.append(
            "INSERT INTO public.address (id, address, postal_code, country, region, city, street, "
            "phone_fax, email, website) VALUES ("
            f"{base.sql_str(addr_id)}, {base.sql_str(r['address'])}, {base.sql_str(r['postal_code'])}, "
            f"{base.sql_str(r['country'])}, {base.sql_str(r['region'])}, {base.sql_str(r['city'])}, "
            f"{base.sql_str(r['street'])}, NULL, NULL, {base.sql_str(r['website'])});"
        )
        lines.append(
            "INSERT INTO public.universities (id, name, description, address_id, rector, "
            "founding_year, student_count) VALUES ("
            f"{base.sql_str(uni_id)}, {base.sql_str(r['name'])}, {base.sql_str(r['description'])}, "
            f"{base.sql_str(addr_id)}, {base.sql_str(r['rector'])}, {base.sql_int(r['founding_year'])}, "
            f"{base.sql_int(r['student_count'])});"
        )
        lines.append("")

    out_path.write_text("\n".join(lines), encoding="utf-8", newline="\n")
    print(f"Записано {out_path.relative_to(base.ROOT)} ({len(remaining)} вузов, {len(remaining) * 2} INSERT)")


if __name__ == "__main__":
    main()
