package ru.nullpointer.storefront.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author Alexander Yastrebov
 */
public class AssertUtils {

    private AssertUtils() {
    }

    public static void assertPropertiesEquals(Object expected, Object actual, String... properties) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertNotNull(properties);
        for (String prop : properties) {
            try {
                Object expectedValue = PropertyUtils.getProperty(expected, prop);
                Object actualValue = PropertyUtils.getProperty(actual, prop);
                assertEquals("Свойства '" + prop + "' должны быть равны", expectedValue, actualValue);
            } catch (Exception ex) {
                fail("Исключение: " + ex);
            }
        }
    }

    public static void assertUnique(List<?> list) {
        assertNotNull(list);
        assertFalse(list.isEmpty());
        Set<Object> set = new HashSet<Object>(list);
        assertEquals(set.size(), list.size());
    }
}
