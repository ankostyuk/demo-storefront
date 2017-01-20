package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import ru.nullpointer.storefront.domain.support.NumberInterval;

/**
 *
 * @author Alexander Yastrebov
 */
abstract class NumberParserSupport extends ParserSupport {

    NumberParserSupport(PropertyEditor editor) {
        super(editor);
    }

    protected NumberInterval parseValues(String minValue, String maxValue) {
        BigDecimal min = (BigDecimal) parseValue(minValue);
        BigDecimal max = (BigDecimal) parseValue(maxValue);

        // раскомментировать для проверки валидности значений
        /*
        NumberParam numberParam = (NumberParam) param;
        if (min != null && max != null) {
        if (min.compareTo(max) > 0) {
        // min > max - обменять местами
        BigDecimal tmp = min;
        min = max;
        max = tmp;
        }
        }

        if (min != null) {
        if (min.compareTo(numberParam.getMinValue()) < 0) {
        // минимальное меньше допустимого - исправить
        min = numberParam.getMinValue();
        }
        }

        if (max != null) {
        if (max.compareTo(numberParam.getMaxValue()) > 0) {
        // максимальное больше допустимого - исправить
        max = numberParam.getMaxValue();
        }
        }
         */

        if (min == null && max == null) {
            // оба значения пустые
            return null;
        }

        return new NumberInterval(min, max);
    }

    protected StringBuilder serializeValues(String minName, String maxName, NumberInterval interval, StringBuilder sb) {
        if (interval.getMin() != null) {
            serializeValue(minName, interval.getMin(), sb);
        }
        
        if (interval.getMax() != null) {
            if (interval.getMin() != null) {
                sb.append(PARAM_DELIMETER);
            }
            serializeValue(maxName, interval.getMax(), sb);
        }
        return sb;
    }
}
