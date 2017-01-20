package ru.nullpointer.storefront.web.tags;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Alexander Yastrebov
 */
public class PriceFunctions {

    private static final BigDecimal ROUND_CENTS_THRESHOLD = new BigDecimal(100);

    private PriceFunctions() {
    }

    /**
     * Форматирует значение цены. Определяет показывать или нет "копейки",
     * отбивает каждые три цифры целой части пробелом.
     * @param number занчение цены
     * @param roundSmallCents  округлять "копейки" при превышении значением некоторого порога
     * @return
     */
    public static String formatPrice(BigDecimal number, boolean roundSmallCents) {
        if (number == null) {
            return null;
        }

        // получить знак, пригодится при отбивке пробелами по 3 разряда
        boolean isNegative = number.signum() == -1;
        if (isNegative) {
            number = number.negate();
        }

        BigDecimal intValue = null;
        BigDecimal frac = null;

        if (roundSmallCents && number.compareTo(ROUND_CENTS_THRESHOLD) >= 0) {
            intValue = number.setScale(0, RoundingMode.HALF_UP);
        } else {
            intValue = number.setScale(0, RoundingMode.DOWN);
            frac = number.subtract(intValue)//
                    .setScale(2, RoundingMode.HALF_UP);
            // проверяем вариант 0.99[5..9] -> 1.00
            if (frac.compareTo(BigDecimal.ONE) == 0) {
                intValue = intValue.add(BigDecimal.ONE);
                frac = null;
            }
        }

        StringBuilder sb = new StringBuilder();
        if (isNegative) {
            sb.append('-');
        }

        String intPart = Long.toString(intValue.longValueExact());
        for (int i = 0; i < intPart.length(); i++) {
            char ch = intPart.charAt(i);
            sb.append(ch);

            // отбить пробелами по три разряда
            // знак учтен отдельно, поэтому можно правильно вычислить индекс
            int pos = intPart.length() - i;
            if (pos > 1 && pos % 3 == 1) {
                sb.append(' ');
            }
        }

        // показать "копейки", если не нулевые
        // показывать 2 разряда
        if (frac != null && frac.compareTo(BigDecimal.ZERO) != 0) {
            sb.append(',');
            // 0.23 -> 23
            sb.append(frac.toString().substring(2, 4));
        }

        return sb.toString();
    }
}
