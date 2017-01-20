package ru.nullpointer.storefront.web.ui;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class SelectOption<E> {

    private String name;
    private E value;

    public SelectOption() {

    }

    public SelectOption(String name, E value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public E getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("name", name)//
                .append("value", value)//
                .toString();
    }
}
