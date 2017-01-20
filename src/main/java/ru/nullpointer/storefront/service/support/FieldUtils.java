package ru.nullpointer.storefront.service.support;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public final class FieldUtils {

    private FieldUtils() {
    }

    public static void nullifyIfEmpty(Object object, String property) {
        Assert.notNull(object);
        try {
            Object value = PropertyUtils.getProperty(object, property);
            if (value != null) {
                if (value instanceof String) {
                    String s = (String) value;
                    if (StringUtils.isBlank(s)) {
                        PropertyUtils.setProperty(object, property, null);
                    }
                } else {
                    throw new IllegalArgumentException();
                }
            }
        } catch (InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
