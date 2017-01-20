#!/bin/sh
./reset_schema.py &&
./update_schema.py &&
./insert_test_data.py &&
./statistics.sh
