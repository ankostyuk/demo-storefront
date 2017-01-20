#! /usr/bin/env python
# -*- coding: UTF-8 -*-
			
from config import config
from static_urls import static_urls
import psycopg2
import psycopg2.extras
from xml.sax.saxutils import escape

def main():
        db = psycopg2.connect (host=config["host"], 
                                port=config["port"], 
                                database=config["database"], 
                                user=config["username"])

        prolog()
	
        gen_static_urls()
        gen_from_query(db, """SELECT acc_id, acc_last_login_date FROM account 
                        WHERE acc_type = 'COMPANY'::t_account_type AND 
                        acc_email_authenticated_date IS NOT NULL 
                        ORDER BY acc_id ASC;""", cb_company)
        gen_from_query(db, """SELECT off_id, off_edit_date FROM offer 
                        WHERE (off_active AND off_status = 'APPROVED'::t_offer_status) 
                        ORDER BY off_id ASC;""", cb_offer)
        gen_from_query(db, """SELECT mod_id FROM model ORDER BY mod_id ASC;""", cb_model)
        gen_from_query(db, """SELECT br_id FROM brand ORDER BY br_id ASC;""", cb_brand)
        gen_from_query(db, """SELECT ci_id, ci_type FROM catalog_item 
                        WHERE ci_active ORDER BY ci_id ASC;""", cb_catalog)

        epilog()

        db.close()

def cb_company(row):
        emit_node("/company/%d" % row[0], row[1], "monthly")

def cb_offer(row):
        emit_node("/offer/%d" % row[0], row[1], "weekly")

def cb_model(row):
        emit_node("/model/%d" % row[0], change_freq="monthly")
        emit_node("/model/%d/offers" % row[0])

def cb_brand(row):
        emit_node("/brand/%d" % row[0], change_freq="monthly")

def cb_catalog(row):
        if row[1] == "CATEGORY":
                emit_node("/category/%d" % row[0], change_freq="daily")
        elif row[1] == "SECTION":
                emit_node("/section/%d" % row[0], change_freq="monthly")

def gen_static_urls():
        for url in static_urls:
                emit_node(url)


def gen_from_query(db, query, callback):
        cur = db.cursor("sitemap_generator_cursor", cursor_factory=psycopg2.extras.DictCursor)
        cur.execute(query)
        for row in cur:
                callback(row)   
        cur.close()

def emit_node(url_part, last_mod=None, change_freq=None, priority=None):
        print "<url>"
        print "  <loc>%s</loc>" % escape(config["base_url"] + url_part)
        if last_mod:
                print "  <lastmod>%s</lastmod>" % last_mod.strftime("%Y-%m-%d")

        if change_freq:
                print "  <changefreq>%s</changefreq>" % change_freq

        if priority:
                print "  <priority>%s</priority>" % priority
        print "</url>"

def prolog():
        print """<?xml version="1.0" encoding="UTF-8"?>"""
        print """  <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">"""

def epilog():
        print "</urlset>"

if __name__ == "__main__":
        main()

