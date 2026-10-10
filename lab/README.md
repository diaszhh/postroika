# Практическая работа: Docker image, volume, container

## Что сделано и зачем

За основу взят мой пет-проект **postroika** (Spring Boot). Его инфраструктура уже описана в `docker-compose.yml`:

| Контейнер | Образ | Полезная нагрузка | Порт на хосте |
|---|---|---|---|
| `postroika-postgres` | `postgres:16` | БД `postroika_db`: таблицы проекта и `lab_orders` на 100 тестовых заказов | `5433 -> 5432` |
| `postroika-redis` | `redis:7.4` | кеш: 50 сессий с TTL, счётчик `page_views` | `6379 -> 6379` |

Основной `docker-compose.yml` не менялся. Всё, что нужно для работы, вынесено в `docker-compose.lab.yml`
(compose override):

- **tmpfs-волюм** `/cache` (64 MB, только в RAM) у `postroika-redis`. В нём скрипт `redis-report.sh` собирает
  черновик отчёта: временные данные не должны попадать на диск.
- **Общий волюм** `postroika_shared` смонтирован в `/shared` у обоих контейнеров. В нём лежат:
  - `/shared/logs/postgres/postgresql.log`: лог PostgreSQL (все запросы и подключения);
  - `/shared/logs/redis/redis.log`: лог Redis;
  - `/shared/metrics/*.log`: отчёты о состоянии сервисов от самописных скриптов;
  - `/shared/scripts/*.sh`: сами скрипты (bash).
- Порт PostgreSQL на хосте `5433`, потому что `5432` занят локально установленным PostgreSQL.

Сеть без доступа наружу создаётся через `docker network create --internal`. У такой bridge-сети нет шлюза
во внешний мир: контейнеры видят только друг друга, а `apt update`, DNS и `ping 8.8.8.8` не работают.

## Структура

```
docker-compose.yml        # исходный compose проекта
docker-compose.lab.yml    # override: tmpfs, общий волюм, логи
lab/
  README.md               # этот файл
  scripts/seed.sh         # наполнение БД и кеша тестовыми данными
  scripts/pg-report.sh    # отчёт о состоянии PostgreSQL -> /shared/metrics
  scripts/redis-report.sh # отчёт о состоянии Redis -> /shared/metrics (через tmpfs)
  screenshots/            # скриншоты
```

## Шаги (команды выполняются из корня проекта postroika)

### 1. Общий волюм и запуск контейнеров

```bash
docker volume create postroika_shared
docker run --rm -v postroika_shared:/shared -v "$PWD/lab/scripts:/src:ro" postgres:16 \
  bash -c 'mkdir -p /shared/logs/postgres /shared/logs/redis /shared/metrics /shared/scripts \
           && cp /src/*.sh /shared/scripts/ && chown -R 999:999 /shared'
docker compose -f docker-compose.yml -f docker-compose.lab.yml up -d
bash lab/scripts/seed.sh
```

### 2. Список контейнеров и проброшенные порты (скрин `01-containers.png`)

```bash
docker container ls
nc -zv localhost 5433
nc -zv localhost 6379
```

### 3. Пинг между контейнерами (скрин `02-ping.png`)

В образах нет `ping`, поэтому сначала ставим его (пока есть интернет):

```bash
docker exec postroika-postgres bash -c 'apt-get update -qq && apt-get install -y -qq iputils-ping'
docker exec postroika-redis    bash -c 'apt-get update -qq && apt-get install -y -qq iputils-ping'
docker exec postroika-postgres ping -c 3 postroika-redis
docker exec postroika-redis    ping -c 3 postroika-postgres
```

### 4. Изолированная сеть (скрины `03-network-attach.png`, `04-no-network.png`)

```bash
docker network create --internal postroika_isolated
docker network connect    postroika_isolated postroika-postgres
docker network connect    postroika_isolated postroika-redis
docker network disconnect postroika_default  postroika-postgres
docker network disconnect postroika_default  postroika-redis
docker network inspect postroika_isolated --format '{{.Internal}} {{range .Containers}}{{.Name}} {{end}}'
```

Проверка, что внешней сети нет:

```bash
docker exec postroika-postgres apt-get update
docker exec postroika-redis ping -c 2 8.8.8.8
docker exec postroika-postgres ping -c 2 postroika-redis   # внутри изолированной сети контейнеры видят друг друга
```

### 5. tmpfs-волюм (скрин `05-tmpfs.png`)

```bash
docker inspect postroika-redis --format 'Tmpfs: {{json .HostConfig.Tmpfs}}'
docker exec postroika-redis df -h /cache
docker exec postroika-redis bash /shared/scripts/redis-report.sh
docker exec postroika-redis ls -la /cache
```

### 6. Общий волюм и данные в нём (скрины `06-shared-volume.png`, `07-shared-data.png`)

```bash
docker volume ls
docker volume inspect postroika_shared
docker inspect postroika-postgres postroika-redis \
  --format '{{.Name}}: {{range .Mounts}}{{.Name}} -> {{.Destination}}; {{end}}'
docker exec postroika-postgres bash /shared/scripts/pg-report.sh
docker exec postroika-redis ls -R /shared
docker exec postroika-redis tail -n 5 /shared/logs/postgres/postgresql.log
```

### 7. Архив для сдачи

```bash
tar -czf docker-lab.tar.gz docker-compose.yml docker-compose.lab.yml lab
```

## Откат

```bash
for c in postroika-postgres postroika-redis; do
  docker network connect postroika_default $c; docker network disconnect postroika_isolated $c
done
docker network rm postroika_isolated
docker compose up -d --force-recreate   # вернуть исходную конфигурацию без override
```
