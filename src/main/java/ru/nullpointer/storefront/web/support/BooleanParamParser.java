package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import javax.servlet.http.HttpServletRequest;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.ParamModel;

/**
 *
 * @author Alexander Yastrebov
 */
class BooleanParamParser extends ParserSupport implements ParamParser {

    BooleanParamParser(PropertyEditor editor) {
        super(editor);
    }

    @Override
    public Object parse(Param param, ParamModel paramModel, HttpServletRequest request) {
        String paramValue = request.getParameter(getParamName(param));
        if (paramValue != null) {
            return parseValue(paramValue);
        }
        return null;
    }

    @Override
    public StringBuilder serialize(Object value, Param param, ParamModel paramModel, StringBuilder sb) {
        return serializeValue(getParamName(param), value, sb);
    }
}
