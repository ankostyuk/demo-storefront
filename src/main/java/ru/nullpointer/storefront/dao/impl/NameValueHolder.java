package ru.nullpointer.storefront.dao.impl;

/**
 *
 * @author alexander
 */
class NameValueHolder {

    private String name;
    private Object value;

    NameValueHolder(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
