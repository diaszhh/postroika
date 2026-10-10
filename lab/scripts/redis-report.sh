#!/bin/bash
# Снимок состояния Redis -> /shared/metrics/redis.log
# Черновик собирается в tmpfs (/cache), готовый отчёт дописывается в общий волюм.
# Запуск: docker exec postroika-redis bash /shared/scripts/redis-report.sh [--loop]
OUT=/shared/metrics/redis.log
TMP=/cache/redis-report.tmp

report() {
  {
    echo "=== $(date '+%F %T') redis report ==="
    redis-cli INFO server  | grep -E '^(redis_version|uptime_in_seconds)'
    redis-cli INFO memory  | grep -E '^used_memory_human'
    redis-cli INFO clients | grep -E '^connected_clients'
    redis-cli INFO keyspace | grep -E '^db'
    echo
  } > "$TMP"
  cat "$TMP" >> "$OUT"
}

if [ "$1" = "--loop" ]; then
  while true; do report; sleep 30; done
else
  report
  tail -n 20 "$OUT"
fi
