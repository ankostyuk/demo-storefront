#!/bin/sh
export PGHOST="127.0.0.1"
export PGPORT="5432"
export PGUSER="postgres"
export PGDATABASE="NULLPOINTER"

if [ ! -n "$1" ]
then
  echo "Usage: `basename $0` DUMP-FILE.sql"
  exit 1
fi

psql -w -q -t -c "DROP SCHEMA IF EXISTS STOREFRONT CASCADE;" &&
psql -d $PGDATABASE -f $1
