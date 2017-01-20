#! /usr/bin/env python
# -*- coding: UTF-8 -*-
from commands import getstatusoutput
from variables import variables
from utils import execute_script
import os
import re

def update_schema():
    schema_version = get_current_schema_version()
    print "Current schema version: %s" % schema_version
    if schema_version == "00.00":
        print "Executing script baseline.sql"
        execute_script("baseline.sql")

    scripts = (fn for fn in os.listdir("../sql/incremental/") if re.match("""\d\d\.\d\d.*\.sql""", fn))
    scripts = sorted(scripts)

    for script in scripts:
        script_version = script[:5]
        schema_version = get_current_schema_version()
        if script_version > schema_version:
            print "Executing script %s" % script
            execute_script("incremental/" + script)
            
    print "Schema is up to date, version: %s" % get_current_schema_version()


def get_current_schema_version():
    status, text = getstatusoutput(
        'psql -w -q -t -A -h %(host)s -p %(port)s -d %(database)s -U %(username)s -c "'
        'SELECT COUNT(*) FROM pg_tables WHERE schemaname=\'%(schema)s\' AND tablename=\'schema_version\''
        '"' % variables)
    if text == '0':
        return '00.00'

    status, text = getstatusoutput(
        'psql -w -q -t -A -h %(host)s -p %(port)s -d %(database)s -U %(username)s -c "'
        'SELECT sv_version FROM %(schema)s.schema_version ORDER BY sv_version DESC LIMIT 1'
        '"' % variables)
    return text

if __name__ == "__main__":
    update_schema()