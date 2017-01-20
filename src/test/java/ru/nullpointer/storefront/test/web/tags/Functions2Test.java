package ru.nullpointer.storefront.test.web.tags;

import org.junit.Test;
import ru.nullpointer.storefront.web.tags.Functions2;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class Functions2Test {

    @Test
    public void test_urlencode() {
        assertNull(Functions2.urlencode(null));

        assertEquals("", Functions2.urlencode(""));
        assertEquals("abc", Functions2.urlencode("abc"));

        assertEquals("x+y", Functions2.urlencode("x y"));
        assertEquals("x%25y", Functions2.urlencode("x%y"));
    }

    @Test
    public void test_xmlid() {
        assertNull(Functions2.xmlid(null));


        assertEquals("xy", Functions2.xmlid("xy"));
        assertEquals("A1xy", Functions2.xmlid("1xy"));
        assertEquals("qwe", Functions2.xmlid("qwe"));
        assertEquals("x.y", Functions2.xmlid("x y"));

        assertEquals("Привет.Мир.", Functions2.xmlid("Привет Мир!"));

    }

    @Test
    public void test_htmlformula() {
        assertNull(Functions2.htmlformula(null));

        assertEquals("", Functions2.htmlformula(""));
        assertEquals("кг", Functions2.htmlformula("кг"));
        assertEquals("м", Functions2.htmlformula("м"));

        assertEquals("&lt;div&gt;test&lt;/div&gt;", Functions2.htmlformula("<div>test</div>"));

        assertEquals("кг/м<sup>2</sup>", Functions2.htmlformula("кг/м^{2}"));
        assertEquals("м<sup>2</sup>", Functions2.htmlformula("м^{2}"));
        assertEquals("Z<sub>min</sub>", Functions2.htmlformula("Z_{min}"));

        assertEquals("<sup>123</sup>", Functions2.htmlformula("^{123}"));
        assertEquals("<sub>123</sub>", Functions2.htmlformula("_{123}"));

        assertEquals("Z_min", Functions2.htmlformula("Z_min"));
        assertEquals("Z_{min", Functions2.htmlformula("Z_{min"));

        assertEquals("&deg;C", Functions2.htmlformula("°C"));
        assertEquals("a&times;b", Functions2.htmlformula("a×b"));
        assertEquals("a&times;b", Functions2.htmlformula("a*b"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_ruplural_invalidInput() {
        Functions2.ruplural(1, null);
        Functions2.ruplural(1, "");
        Functions2.ruplural(1, "гусь");
        Functions2.ruplural(1, "гусь/гуся");
    }

    @Test
    public void test_ruplural() {
        assertEquals(null, Functions2.ruplural(null, "гусь/гуся/гусей"));
        assertEquals("гусь", Functions2.ruplural(1, "гусь/гуся/гусей"));
        assertEquals("гусь", Functions2.ruplural(21, "гусь/гуся/гусей"));
        assertEquals("гусь", Functions2.ruplural(101, "гусь/гуся/гусей"));

        assertEquals("гуся", Functions2.ruplural(2, "гусь/гуся/гусей"));
        assertEquals("гуся", Functions2.ruplural(22, "гусь/гуся/гусей"));
        assertEquals("гуся", Functions2.ruplural(102, "гусь/гуся/гусей"));

        assertEquals("гусей", Functions2.ruplural(5, "гусь/гуся/гусей"));
        assertEquals("гусей", Functions2.ruplural(11, "гусь/гуся/гусей"));
        assertEquals("гусей", Functions2.ruplural(20, "гусь/гуся/гусей"));
        assertEquals("гусей", Functions2.ruplural(100, "гусь/гуся/гусей"));
    }

    @Test
    public void test_formatPhone() {
        assertEquals(null, Functions2.formatPhone(null));
        assertEquals("", Functions2.formatPhone(" \t    \t"));
        assertEquals("", Functions2.formatPhone(" \t  x  \t"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123-45-67"));
        assertEquals("+7 1234 12-34-56", Functions2.formatPhone("+7 1234 12-34-56"));
        assertEquals("+7 12345 1-23-45", Functions2.formatPhone("+7 12345 1-23-45"));
        assertEquals("+7 123456 12-34", Functions2.formatPhone("+7 123456 12-34"));
        assertEquals("123-45-67", Functions2.formatPhone("123-45-67"));
        assertEquals("12-34-56", Functions2.formatPhone("12-34-56"));
        assertEquals("1-23-45", Functions2.formatPhone("1-23-45"));
        assertEquals("12-34", Functions2.formatPhone("12-34"));
        assertEquals("1-23", Functions2.formatPhone("1-23"));
        assertEquals("12", Functions2.formatPhone("12"));
        assertEquals("1", Functions2.formatPhone("1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+71231234567"));
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8(123)1234567"));
        assertEquals("+7 1234 12-34-56", Functions2.formatPhone("(1234)123456"));
        assertEquals("+7 12345 1-23-45", Functions2.formatPhone("+7 (12345)12345"));
        assertEquals("+7 123456 12-34", Functions2.formatPhone("8 (123456)1234"));
        assertEquals("+7 1234567 1-23", Functions2.formatPhone("(1234567)123"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7123 1234567"));
        assertEquals("+7 1234 12-34-56", Functions2.formatPhone("81234 123456"));
        assertEquals("+7 12345 1-23-45", Functions2.formatPhone("12345 12345"));
        assertEquals("+7 123456 12-34", Functions2.formatPhone("+7 123456 1234"));
        assertEquals("+7 123 456-71-23", Functions2.formatPhone("8 1234567 123"));
        assertEquals("+7 123 456-78-12", Functions2.formatPhone("+7 12345678 12"));
        assertEquals("+7 123 456-78-91", Functions2.formatPhone("123456789 1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("123-1234567"));
        assertEquals("+7 1234 12-34-56", Functions2.formatPhone("1234-123456"));
        assertEquals("+7 12345 1-23-45", Functions2.formatPhone("12345-12345"));
        assertEquals("+7 123456 12-34", Functions2.formatPhone("123456-1234"));
        assertEquals("+7 123 456-71-23", Functions2.formatPhone("1234567-123"));
        assertEquals("+7 123 456-78-12", Functions2.formatPhone("12345678-12"));
        assertEquals("+7 123 456-78-91", Functions2.formatPhone("123456789-1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8-(123)-123-45-67"));
        assertEquals("+7 812 123-45-67", Functions2.formatPhone("8-(812)-123-45-67"));
        assertEquals("8 800 123-45-67", Functions2.formatPhone("8-(800)-123-45-67"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8 123 123 45 67"));
        assertEquals("+7 812 123-45-67", Functions2.formatPhone("8 812 123 45 67"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8-123-123-45-67"));
        assertEquals("+7 812 123-45-67", Functions2.formatPhone("8-812-123-45-67"));

        assertEquals("123-45-67", Functions2.formatPhone("1234567"));
        assertEquals("12-34-56", Functions2.formatPhone("123456"));
        assertEquals("1-23-45", Functions2.formatPhone("12345"));
        assertEquals("12-34", Functions2.formatPhone("1234"));
        assertEquals("1-23", Functions2.formatPhone("123"));
        assertEquals("12", Functions2.formatPhone("12"));
        assertEquals("1", Functions2.formatPhone("1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8   1 2 3  1 2  3 45 67"));
        assertEquals("123-45-67", Functions2.formatPhone("1 2 3 4 5 6 7"));
        assertEquals("12-34-56", Functions2.formatPhone("1 2 3 4 5 6"));
        assertEquals("1-23-45", Functions2.formatPhone("1 2 3 4 5"));
        assertEquals("12-34", Functions2.formatPhone("1 2 3 4"));
        assertEquals("1-23", Functions2.formatPhone("1 2 3"));
        assertEquals("12", Functions2.formatPhone("1 2"));
        assertEquals("1", Functions2.formatPhone("1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("1-2-3-1-2-3-4-5-6-7"));
        assertEquals("123-45-67", Functions2.formatPhone("1-2-3-4-5-6-7"));
        assertEquals("12-34-56", Functions2.formatPhone("1-2-3-4-5-6"));
        assertEquals("1-23-45", Functions2.formatPhone("1-2-3-4-5"));
        assertEquals("12-34", Functions2.formatPhone("1-2-3-4"));
        assertEquals("1-23", Functions2.formatPhone("1-2-3"));
        assertEquals("12", Functions2.formatPhone("1-2"));
        assertEquals("1", Functions2.formatPhone("1"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("8   123  123-4567"));

        assertEquals("+7 123 456-78-90", Functions2.formatPhone("123-45-6-78-90"));

        assertEquals("+7 234 567-89-01", Functions2.formatPhone("12345678901"));
        assertEquals("+7 123 456-78-90 1", Functions2.formatPhone("812345678901"));

        assertEquals("+7 495 123-45-67", Functions2.formatPhone("84951234567"));
        assertEquals("8 800 123-45-67", Functions2.formatPhone("88001234567"));
        assertEquals("8 809 123-45-67", Functions2.formatPhone("+78091234567"));

        assertEquals("+7 123 123-45-67, +7 123 123-45-67", Functions2.formatPhone("+7 123 123-45-67; +7 123 123-45-67"));
        assertEquals("+7 123 123-45-67, +7 987 654-32-10", Functions2.formatPhone("Вася: +7 123 123-45-67; Петя: 9876543210"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("81231234567"));
        assertEquals("+7 823 123-45-67", Functions2.formatPhone("88231234567"));
        assertEquals("+7 123 123-45-67 доб.123", Functions2.formatPhone("81231234567 доб.123"));
        assertEquals("+7 723 123-45-67 доб.123", Functions2.formatPhone("87231234567 доб.123"));

        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123 --- 45 --- 67"));

        // UNICODE: с неразрывным пробелом
        assertEquals("+7 812 963-61-56", Functions2.formatPhone("+7 (812) 963-61-56"));
        assertEquals("+7 812 963-61-56", Functions2.formatPhone("+7\u00A0 \u00A0\u00A0  \u00A0(812)   \u00A0\u00A0 963-61-56"));
        // UNICODE: HTML-пробелы
        assertEquals("+7 876 543-21-00", Functions2.formatPhone(" \u00A0 \u2002 \u2003 \u200A 8 \u00A0 \u2002 \u2003 \u200A 876 \u00A0 \u2002 \u2003 \u200A 543 \u00A0 \u2002 \u2003 \u200A 21 \u00A0 \u2002 \u2003 \u200A 00 \u00A0 \u2002 \u2003 \u200A "));

        // UNICODE: HTML дефис, тире, минус, ...
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123―45―67")); // u2015
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123—45—67")); // u2014
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123–45–67")); // u2013
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123‒45‒67")); // u2012
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123‐45‐67")); // u2010
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123−45−67")); // u2212
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123⁃45⁃67")); // u2043
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123\u00AD45\u00AD67")); // u00AD
        assertEquals("+7 123 123-45-67", Functions2.formatPhone("+7 123 123 \u2015 \u2014 \u2013 \u2012 \u2010 \u2212 \u2043 \u00AD 4567"));
    }
}
