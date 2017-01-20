package ru.nullpointer.storefront.web.tags;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class Functions2 {

    private static Logger logger = LoggerFactory.getLogger(Functions2.class);

    public static boolean contains(Collection c, Object o) {
        return (c != null ? c.contains(o) : false);
    }

    public static String urlencode(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }

        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("This should never happen");
            return null;
        }
    }

    public static String xmlid(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }

        /*
         * ID and NAME must begin with a letter ([A-Za-z]) and may be followed by
         * any number of letters, digits ([0-9]), hyphens ("-"), underscores ("_"),
         * colons (":"), and periods (".").
         */

        StringBuilder sb = new StringBuilder();

        char ch = s.charAt(0);
        if (!Character.isLetter(ch)) {
            sb.append('A');
        }

        int len = s.length();
        for (int i = 0; i < len; i++) {
            ch = s.charAt(i);
            if (!Character.isLetter(ch)
                    && !Character.isDigit(ch)
                    && ch != '-' && ch != '_'
                    && ch != ':' && ch != '.') {
                ch = '.';
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Форматирует строку в html
     * Поддерживаемые управляющие символы:
     *      Возведение в степень: a^{2} -> a<sup>2</sup>
     *      Нижний индекс: a_{2} -> a<sub>2</sub>
     *      Знак умножения: a*b -> a×b
     * @param s
     * @return
     */
    public static String htmlformula(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }

        s = s.replace('*', '×');

        s = HtmlUtils.htmlEscape(s);

        s = s.replaceAll("\\^\\{([^\\}]+)\\}", "<sup>$1</sup>");
        s = s.replaceAll("_\\{([^\\}]+)\\}", "<sub>$1</sub>");

        return s;
    }

    /**
     * Форматирует существительное в соответствии с числительным
     * на русском языке
     * @param number числительное
     * @param variants строка вариантов. Варианты должны быть разделены символом "/".
     *          Например: "гусь/гуся/гусей".
     * @return один из вариантов соответствующий числительному
     */
    public static String ruplural(Number number, String variants) {
        if (number == null) {
            return null;
        }
        String[] vars = StringUtils.split(variants, '/');

        Assert.notNull(vars);
        Assert.isTrue(vars.length == 3);

        int n = number.intValue();

        int index = n % 10 == 1 && n % 100 != 11 ? 0 : n % 10 >= 2 && n % 10 <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2;

        return vars[index];
    }

    public static String buildUrlParams(String paramName, String[] values) {
        if (StringUtils.isBlank(paramName) || values == null || values.length < 1) {
            return null;
        }

        Assert.hasText(values[0]);
        StringBuilder sb = new StringBuilder(paramName).append('=').append(values[0]);

        for (int i = 1; i < values.length; i++) {
            Assert.hasText(values[i]);
            sb.append('&').append(paramName).append('=').append(values[i]);
        }

        return sb.toString();
    }

    /**
     * Форматирует строку телефонного номера в одно из представлений:
     *
     * +7 123 123-45-67
     * +7 1234 12-34-56
     * +7 12345 1-23-45
     * +7 123456 12-34
     *
     * 8 800 123-45-67
     *
     * 123-45-67
     * 12-34-56
     * 1-23-45
     * 12-34
     * 1-23
     * 12
     * 1
     *
     * Если в строке несколько телефонных номеров - отделяет форматированные номера ','
     *
     * @param phoneNumber неформатированный номер телефона
     * @return форматированный номер телефона
     */
    public static String formatPhone(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        try {
            // нормализация
            String phoneN = phoneNumber.replaceAll("^[^0-9]+", "").replaceAll("[\\s\\u00A0\\u2002\\u2003\\u200A]+", " ").replaceAll("[\\-\\u2015\\u2014\\u2013\\u2012\\u2010\\u2212\\u2043\\u00AD]+", "-").replaceAll("\\s?-\\s?", "-").replaceAll("\\s?[,;*|\\\\/]+\\s?", ",").replaceAll("^\\s+|\\s+$", "");
    
            if (phoneN.isEmpty()) {
                return "";
            }

            Matcher matcher = null;
            final Pattern digitSequencePattern = Pattern.compile("[0-9]+");
            final Pattern codePattern = Pattern.compile(".*[(]?([0-9][ -]?)+[)]|^[78]?[ ]?([0-9][-]?){3,6}+[ ]|^[78]?[-]?([0-9][ ]?){3,6}+[-]");
            final Pattern fullNumberPattern = Pattern.compile("([0-9][() -]*){10,11}[ ]|([0-9][() -]*){10,11}");
            final Pattern smallNumberPattern = Pattern.compile("([0-9][() -]*){1,9}");
            final String[][] numFormats = {
                {"([0-9]{3})([0-9]{2})([0-9]{2})", "$1-$2-$3"},
                {"([0-9]{2})([0-9]{2})([0-9]{2})", "$1-$2-$3"},
                {"([0-9]{1})([0-9]{2})([0-9]{2})", "$1-$2-$3"},
                {"([0-9]{2})([0-9]{2})", "$1-$2"},
                {"([0-9]{1})([0-9]{2})", "$1-$2"}
            };
            String[] code8 = {"800", "801", "802", "803", "804", "805", "806", "807", "808", "809"};
            ArrayList<String> code8List = new ArrayList<String>(Arrays.asList(code8));
    
            // Разделить по количеству номеров
            String[] phones = phoneN.split(",");
    
            StringBuilder result = new StringBuilder();
    
            for (int i = 0; i < phones.length; i++) {
                String phone = phones[i].replaceAll("^[^0-9]+", "");
                StringBuffer format = new StringBuffer();
    
                int digitLen = 0;
                matcher = digitSequencePattern.matcher(phone.replaceAll("[() -]", ""));
                if (matcher.find()) {
                    digitLen = matcher.end() - matcher.start();
                }
    
                if (digitLen == 0) {
                    format.append(phone.trim());
                } else {
                    String code = "";
                    String num = "";
                    String extra = "";
    
                    if (digitLen >= 10) {
                        int codeEnd = 0;
                        matcher = codePattern.matcher(phone);
                        if (matcher.find()) {
                            codeEnd = matcher.end();
                            code = matcher.group();
                        }
                        matcher = fullNumberPattern.matcher(phone);
                        if (matcher.find()) {
                            num = codeEnd > 0 ? phone.substring(codeEnd, matcher.end()) : matcher.group();
                            extra = phone.substring(matcher.end());
                        }
                    } else {
                        matcher = smallNumberPattern.matcher(phone);
                        if (matcher.find()) {
                            num = matcher.group();
                            extra = phone.substring(matcher.end());
                        }
                    }
    
                    code = code.replaceAll("[^0-9]", "");
                    num = num.replaceAll("[^0-9]", "");
    
                    if (num.isEmpty()) {
                        format.append(phone.trim());
                    } else {
                        if (code.isEmpty() && num.length() >= 10) {
                            code = num.substring(0, num.length() - 7);
                            num = num.substring(num.length() - 7);
                        }
    
                        boolean pref = (code.length() + num.length() == 11);
    
                        int len = num.length();
                        if (len > 2 && len <= 7) {
                            num = num.replaceAll(numFormats[7 - len][0], numFormats[7 - len][1]);
                        } else
                        if (len > 7) {
                            num = num.substring(0, len - 7) + " " + num.substring(len - 7).replaceAll(numFormats[0][0], numFormats[0][1]);
                        }
    
                        if (code.isEmpty()) {
                            format.append(num);
                        } else {
                            if (pref) {
                                code = code.substring(1);
                            }
                            if (code8List.contains(code)) {
                                format.append("8 ").append(code);
                            } else {
                                format.append("+7 ").append(code);
                            }
                            format.append(" ").append(num);
                        }
                        if (!extra.isEmpty()) {
                            format.append(" ").append(extra);
                        }
                    }
                }
    
                if (i > 0) {
                    result.append(", ");
                }
                result.append(format);
            }
            return result.toString();
        } catch (Exception e) {
            logger.error("Ошибка при форматировании телефонного номера: {}", e);
            return phoneNumber;
        }
    }
}
