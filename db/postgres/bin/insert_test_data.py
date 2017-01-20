#! /usr/bin/env python
# -*- coding: UTF-8 -*-
import os
import re
from utils import execute_script

def insert_test_data():
    scripts = (fn for fn in os.listdir("../sql/test_data/") if re.match("""\d\d.*\.sql""", fn))
    scripts = sorted(scripts)
    for script in scripts:
        print "Executing script %s" % script
        execute_script("test_data/" + script)

if __name__ == "__main__":
    insert_test_data()