#!/bin/sh
PACKAGE_NAME="production-pkg"

DIR=$(cd $(dirname "$0"); pwd)

echo cобираем проект...
cd $DIR/../
mvn -P production clean install

echo === копируем файлы ===
rm -rf target/$PACKAGE_NAME
mkdir target/$PACKAGE_NAME
cp target/storefront.war target/$PACKAGE_NAME

mkdir target/$PACKAGE_NAME/sql
cp -R db/postgres/sql/incremental target/$PACKAGE_NAME/sql

mkdir target/$PACKAGE_NAME/bin
cp db/postgres/bin/variables.py target/$PACKAGE_NAME/bin
cp db/postgres/bin/utils.py target/$PACKAGE_NAME/bin
cp db/postgres/bin/update_schema.py target/$PACKAGE_NAME/bin
cp bin/redeploy.sh target/$PACKAGE_NAME/bin
chmod +x target/$PACKAGE_NAME/bin/redeploy.sh

mkdir target/$PACKAGE_NAME/bin/sitemap
cp -R utils/sitemap/*.py target/$PACKAGE_NAME/bin/sitemap
 
echo === пакуем все ===
cd target
tar -czvf $PACKAGE_NAME.tar.gz $PACKAGE_NAME

echo === пакет обновления $DIR/target/$PACKAGE_NAME.tar.gz готов! ===
