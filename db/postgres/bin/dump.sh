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
pg_dump > $1
