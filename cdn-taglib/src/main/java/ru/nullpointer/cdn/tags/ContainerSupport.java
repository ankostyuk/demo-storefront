package ru.nullpointer.cdn.tags;

import java.util.Map;
import org.apache.commons.lang.Validate;

/**
 *
 * @author Alexander Yastrebov
 */
public abstract class ContainerSupport implements Container {

    private Map<String, String> properties;

    protected Boolean getBoolean(String name) {
        String value = getTrimmed(name);
        if (value != null) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    protected boolean getBoolean(String name, boolean nullValue) {
        Boolean result = getBoolean(name);
        return result != null ? result : nullValue;
    }

    protected String getTrimmed(String name) {
        String value = getValue(name);
        if (value != null) {
            return value.trim();
        }
        return null;
    }

    protected String getValue(String name) {
        Validate.notNull(name);
        return properties.get(name);
    }

    protected abstract void afterPropertiesSet();

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
        afterPropertiesSet();
    }
}
