#!/bin/sh

DIR=$(cd $(dirname "$0"); pwd)

rsync -rltvz -e ssh --delete --progress $DIR/../target/production-pkg/  root@bildika.ru:sync

