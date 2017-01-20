#! /usr/bin/env python
# -*- coding: UTF-8 -*-
from commands import getstatusoutput
from variables import variables

def reset_schema():
    status, text = getstatusoutput(
        'psql -w -h %(host)s -p %(port)s -d %(database)s -U %(username)s -c "'
        'DROP SCHEMA IF EXISTS \"%(schema)s\" CASCADE;'
        'CREATE SCHEMA \"%(schema)s\" AUTHORIZATION \"%(schema)s\";'
        '"' % variables)
    print status, text

if __name__ == "__main__":
    reset_schema()