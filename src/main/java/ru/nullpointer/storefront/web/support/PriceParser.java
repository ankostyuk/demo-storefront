package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import javax.servlet.http.HttpServletRequest;
import ru.nullpointer.storefront.domain.support.NumberInterval;

/**
 *
 * @author Alexander Yastrebov
 */
class PriceParser extends NumberParserSupport {

    private static final String MIN_PRICE_PARAM = "price-min";
    private static final String MAX_PRICE_PARAM = "price-max";

    PriceParser(PropertyEditor editor) {
        super(editor);
    }

    public NumberInterval parse(HttpServletRequest request) {
        String minValue = request.getParameter(MIN_PRICE_PARAM);
        String maxValue = request.getParameter(MAX_PRICE_PARAM);

        return parseValues(minValue, maxValue);
    }

    public StringBuilder serialize(NumberInterval value, StringBuilder sb) {
        return serializeValues(MIN_PRICE_PARAM, MAX_PRICE_PARAM, value, sb);
    }
}
