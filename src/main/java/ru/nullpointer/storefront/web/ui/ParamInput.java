package ru.nullpointer.storefront.web.ui;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class ParamInput {

    private Map<Integer, Object> paramValueMap;
    private boolean errors;

    public ParamInput() {
        paramValueMap = new HashMap<Integer, Object>();
    }

    public ParamInput(Map<Integer, Object> paramValueMap) {
        this();
        Assert.notNull(paramValueMap);
        this.paramValueMap.putAll(paramValueMap);
    }

    public Map<Integer, Object> getParamValueMap() {
        return paramValueMap;
    }

    public boolean hasErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }

    /**
     * Сокращение для доступа к значению параметра с помощью EL
     * (например: paramInput.p[10])
     * @return
     */
    public Map<Integer, Object> getP() {
        return getParamValueMap();
    }

    public static String buildParamPath(Integer paramId) {
        return new StringBuilder("p[")//
                .append(paramId)//
                .append("]")//
                .toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("paramValueMap", paramValueMap) //
                .toString();
    }
}
