#!/usr/bin/env python
# -*- coding: UTF-8 -*-

from BeautifulSoup import BeautifulSoup
from soupselect import select
from urllib2 import urlopen
import re

def main():
    url = "http://www.kdvor.ru/goods/vendors.html"
    doc = BeautifulSoup(urlopen(url))
    p = select(doc, "td.blocks_middle_td p")[1]
    for a in select(p, "a"):
        parseVendor(a["href"])


def parseVendor(idParam):
    url = "http://www.kdvor.ru/goods/vendors.html" + idParam
    doc = BeautifulSoup(urlopen(url))
    td = selectOne(doc, "td.blocks_middle_td td.text")
    name = selectOne(td, "h1").string

    p = str(td)
    phone = getValue("Телефон", p)
    site = getValue("WWW", p)
    email = getValue("Эл. почта", p)

    printVendor(name, phone, site, email)


def printVendor(name, phone=None, site=None, email=None):
    print name, " # ", phone if phone else "", " # ", site if site else "", "#", email if email else ""


def getValue(label, string):
    m = re.search(label + "\s*:\s*<b>([^<]+)", string)
    return m.group(1) if m else None


def selectOne(el, selector):
    result = select(el, selector)
    return result[0] if result else ''


if __name__ == "__main__":
    main()

