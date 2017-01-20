package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import javax.servlet.http.HttpServletRequest;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.ParamModel;

/**
 *
 * @author Alexander Yastrebov
 */
class NumberParamParser extends NumberParserSupport implements ParamParser {

    private static final String MIN_SUFFIX = "-min";
    private static final String MAX_SUFFIX = "-max";

    NumberParamParser(PropertyEditor editor) {
        super(editor);
    }

    @Override
    public Object parse(Param param, ParamModel paramModel, HttpServletRequest request) {
        String paramName = getParamName(param);
        String minName = paramName + MIN_SUFFIX;
        String maxName = paramName + MAX_SUFFIX;

        String minValue = request.getParameter(minName);
        String maxValue = request.getParameter(maxName);

        return parseValues(minValue, maxValue);
    }

    @Override
    public StringBuilder serialize(Object value, Param param, ParamModel paramModel, StringBuilder sb) {
        String paramName = getParamName(param);
        String minName = paramName + MIN_SUFFIX;
        String maxName = paramName + MAX_SUFFIX;

        serializeValues(minName, maxName, (NumberInterval) value, sb);

        return sb;
    }
}
