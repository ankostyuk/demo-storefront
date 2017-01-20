package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import ru.nullpointer.storefront.domain.param.Param;

/**
 *
 * @author Alexander Yastrebov
 */
abstract class ParserSupport {

    static final String PARAM_DELIMETER = "&";
    //
    private static final String PARAM_EQUALITY = "=";
    private static final String PARAM_PREFIX = "p";
    //
    private PropertyEditor editor;

    ParserSupport(PropertyEditor editor) {
        this.editor = editor;
    }

    protected String getParamName(Param param) {
        return PARAM_PREFIX + param.getId();
    }

    protected Object parseValue(String value) {
        try {
            editor.setAsText(value);
            return editor.getValue();
        } catch (IllegalArgumentException ex) {
        }
        return null;
    }

    protected StringBuilder serializeValue(String key, Object value, StringBuilder sb) {
        sb.append(key);
        sb.append(PARAM_EQUALITY);

        editor.setValue(value);
        sb.append(editor.getAsText());

        return sb;
    }
}
