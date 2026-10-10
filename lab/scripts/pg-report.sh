#!/bin/bash
# Снимок состояния PostgreSQL -> /shared/metrics/postgres.log
# Запуск: docker exec postroika-postgres bash /shared/scripts/pg-report.sh [--loop]
OUT=/shared/metrics/postgres.log

report() {
  {
    echo "=== $(date '+%F %T') postgres report ==="
    psql -U postgres -d postroika_db -At -c "select 'db_size: ' || pg_size_pretty(pg_database_size('postroika_db'))"
    psql -U postgres -d postroika_db -At -c "select 'connections: ' || count(*) from pg_stat_activity"
    psql -U postgres -d postroika_db -At -c "select 'table ' || relname || ': ' || n_live_tup || ' rows' from pg_stat_user_tables order by relname"
    echo
  } >> "$OUT"
}

if [ "$1" = "--loop" ]; then
  while true; do report; sleep 30; done
else
  report
  tail -n 20 "$OUT"
fi
