package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.domain.support.ParamModel;

/**
 *
 * @author Alexander Yastrebov
 */
class SelectParamParser extends ParserSupport implements ParamParser {

    SelectParamParser(PropertyEditor editor) {
        super(editor);
    }

    @Override
    public Object parse(Param param, ParamModel paramModel, HttpServletRequest request) {
        String[] paramValues = request.getParameterValues(getParamName(param));
        if (paramValues != null) {
            List<Integer> result = new ArrayList<Integer>();
            List<Integer> allowedValues = getAllowedValues(param, paramModel);
            for (String paramValue : paramValues) {
                Integer value = (Integer) parseValue(paramValue);
                if (allowedValues.contains(value)) {
                    result.add(value);
                }
            }
            if (!result.isEmpty()) {
                return Collections.unmodifiableList(result);
            }
        }
        return null;
    }

    @Override
    public StringBuilder serialize(Object value, Param param, ParamModel paramModel, StringBuilder sb) {
        List<Integer> paramValues = (List<Integer>) value;
        String paramName = getParamName(param);
        List<Integer> allowedValues = getAllowedValues(param, paramModel);
        if (!paramValues.isEmpty()) {
            // первое значение
            Integer paramValue = paramValues.get(0);
            if (allowedValues.contains(paramValue)) {
                serializeValue(paramName, paramValue, sb);
            }
            // все остальные
            for (int i = 1; i < paramValues.size(); i++) {
                paramValue = paramValues.get(i);
                if (allowedValues.contains(paramValue)) {
                    sb.append(PARAM_DELIMETER);
                    serializeValue(paramName, paramValue, sb);
                }
            }
        }
        return sb;
    }

    private List<Integer> getAllowedValues(Param param, ParamModel paramModel) {
        List<Integer> result = new ArrayList<Integer>();
        for (ParamSelectOption option : paramModel.getSelectOptionMap().get(param.getId())) {
            result.add(option.getId());
        }
        return result;
    }
}
