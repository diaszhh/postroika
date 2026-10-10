#!/bin/bash
# Наполняет контейнеры тестовыми данными (полезная нагрузка).
# Запуск с хоста: bash lab/scripts/seed.sh
set -e

docker exec -i postroika-postgres psql -U postgres -d postroika_db <<'SQL'
create table if not exists lab_orders (
  id serial primary key,
  customer text not null,
  amount numeric(10,2) not null,
  created_at timestamp default now()
);
insert into lab_orders (customer, amount)
select 'customer_' || g, round((random() * 1000)::numeric, 2)
from generate_series(1, 100) g;
select count(*) as orders from lab_orders;
SQL

for i in $(seq 1 50); do
  docker exec postroika-redis redis-cli SET "session:$i" "user_$i" EX 3600 > /dev/null
done
docker exec postroika-redis redis-cli INCRBY page_views 1234
docker exec postroika-redis redis-cli DBSIZE
