package ru.nullpointer.storefront.web.ui;

import java.beans.PropertyEditorSupport;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class EnumEditor extends PropertyEditorSupport {

    private Class<? extends Enum> clazz;
    private boolean allowEmpty;

    public EnumEditor(Class<? extends Enum> clazz) {
        this(clazz, true);
    }

    public EnumEditor(Class<? extends Enum> clazz, boolean allowEmpty) {
        Assert.notNull(clazz);
        this.clazz = clazz;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            if (allowEmpty) {
                setValue(null);
                return;
            } else {
                throw new IllegalArgumentException("allowEmpty property is false");
            }
        }
        setValue(Enum.valueOf(clazz, text));
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value == null ? "" : value.toString();
    }
}
