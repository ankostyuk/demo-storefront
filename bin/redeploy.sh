#!/bin/sh

WEBAPP_WORKDIR=/opt/storefront
WEBAPP_TOMCAT=/opt/tomcat

DIR=$(cd $(dirname "$0"); pwd)
cd $DIR

echo offline > $WEBAPP_WORKDIR/maintenance

/etc/init.d/tomcat stop
sleep 10

python update_schema.py
rm -rf $WEBAPP_TOMCAT/work/*
rm -rf $WEBAPP_TOMCAT/webapps/*
cp ../storefront.war $WEBAPP_TOMCAT/webapps/ROOT.war

/etc/init.d/tomcat start
sleep 10
rm -f $WEBAPP_WORKDIR/maintenance
