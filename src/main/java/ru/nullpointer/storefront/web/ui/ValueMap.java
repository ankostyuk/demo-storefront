package ru.nullpointer.storefront.web.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class ValueMap {

    private Map<String, Object> internalMap;

    @SuppressWarnings("unchecked")
    public ValueMap(final Class valueType) {
        Assert.notNull(valueType);

        internalMap = LazyMap.decorate(new InternalMap(), new Factory() {

            @Override
            public Object create() {
                try {
                    return valueType.newInstance();
                } catch (InstantiationException ex) {
                    throw new RuntimeException(ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public Iterable<String> getKeys() {
        return internalMap.keySet();
    }

    public Map<String, Object> getValue() {
        return internalMap;
    }

    public static String buildObjectPath(Object key) {
        return new StringBuilder("value[")//
                .append(key)//
                .append("]")//
                .toString();
    }

    public static String buildPropertyPath(Object key, String property) {
        return new StringBuilder(buildObjectPath(key))//
                .append(".")//
                .append(property)//
                .toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("internalMap", internalMap)//
                .toString();
    }

    public static class InternalMap extends HashMap<String, Object> {

        private static final long serialVersionUID = 16022010L;
        // для сортировки в порядке добавления
        private LinkedHashSet<String> keys = new LinkedHashSet<String>();

        @Override
        public Object put(String key, Object value) {
            keys.add(key);
            return super.put(key, value);
        }

        @Override
        public Object remove(Object key) {
            String strKey = (String) key;
            keys.remove(strKey);
            return super.remove(strKey);
        }

        @Override
        public void clear() {
            keys.clear();
            super.clear();
        }

        @Override
        public Set<String> keySet() {
            return Collections.unmodifiableSet(keys);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                    .append("keys", keys)//
                    .append("map", super.toString())//
                    .toString();
        }
    }
}
