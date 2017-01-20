package ru.nullpointer.storefront.web.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class ObjectMap<O> {

    private Map<Object, O> objectMap;
    // для сортировки в порядке добавления
    private List<Object> keys;
    
    public ObjectMap() {
        objectMap = new HashMap<Object, O>();
        keys = new ArrayList<Object>();
    }

    public O put(Object key, O value) {
        keys.add(key);
        return objectMap.put(key, value);
    }

    public O remove(Object key) {
        keys.remove(key);
        return objectMap.remove(key);
    }

    public O get(Object key) {
        return objectMap.get(key);
    }

    /**
     * Возвращает ключи карты, отсортированные в порядке добавления
     * @return
     */
    public Iterable<Object> getKeys() {
        return keys;
    }

    public Map<Object, O> getValue() {
        return objectMap;
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
                .append("objectMap", objectMap)//
                .toString();
    }
}
