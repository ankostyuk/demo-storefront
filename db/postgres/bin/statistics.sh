#!/bin/sh
export PGHOST="127.0.0.1"
export PGPORT="5432"
export PGUSER="storefront"
export PGDATABASE="NULLPOINTER"

QUERY="psql -w -q -t -c"

echo -- Статистика --
echo Количество моделей в каталоге: `$QUERY "select count(*) from model"`
echo Количество брендов в каталоге: `$QUERY "select count(*) from brand"`
echo 

echo Количество поставщиков в каталоге: `$QUERY "select count(*) from company"`
echo Количество предложений в каталоге: `$QUERY "select count(*) from offer"`
echo

echo "Количество опубликованных предложений (всего | %):" `$QUERY "select count(*), round((100. * count(*) / (select count(*) from offer)), 1)
from offer where off_active and off_status = 'APPROVED'"`

echo Количество поставщиков с предложениями: `$QUERY "select count(distinct off_company_id) from offer"`
echo "Количество предложений на поставщика (сред. | мин. | макс.):" `$QUERY "select round(avg(count), 1), min(count), max(count) from 
(select count(off_id) from offer group by off_company_id) as count"`

echo "Количество простых предложений (всего | %):" `$QUERY "select count(*), round((100. * count(*) / (select count(*) from offer)), 1) 
from offer where off_param_set_id is null and off_model_id is null"`

echo "Количество предложений с параметрами (всего | %):" `$QUERY "select count(*), round((100. * count(*) / (select count(*) from offer)), 1) 
from offer where off_param_set_id is not null"`

echo "Количество предложений связанных с моделью (всего | %):" `$QUERY "select count(*), round((100. * count(*) / (select count(*) from offer)), 1) 
from offer where off_model_id is not null"`

echo "Количество предложений связанных с брендом (всего | %):" `$QUERY "select count(*), round((100. * count(*) / (select count(*) from offer)), 1) 
from offer where off_brand_id is not null"`
