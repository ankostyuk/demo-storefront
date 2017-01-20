package ru.nullpointer.storefront.dao.impl;

import ru.nullpointer.storefront.domain.param.Param;

/**
 *
 * @author Alexander Yastrebov
 */
class ParamValueHolder {

    private Param param;
    private Object value;

    ParamValueHolder(Param param, Object value) {
        this.param = param;
        this.value = value;
    }

    public Param getParam() {
        return param;
    }

    public Object getValue() {
        return value;
    }
}
