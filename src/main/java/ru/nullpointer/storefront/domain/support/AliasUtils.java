package ru.nullpointer.storefront.domain.support;

import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class AliasUtils {

    /**
     * Возвращает значение константы по псевдониму или значение по-умолчанию
     * @param alias псевдоним. Может быть <code>null</code>
     * @param defaultValue значение по-умолчанию
     * @return
     * @throws IllegalArgumentException если <code>defaultValue</code> равен <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum & EnumAlias> T fromAlias(String alias, T defaultValue) {
        Assert.notNull(defaultValue);
        return fromAlias(alias, (Class<T>) defaultValue.getClass(), defaultValue);
    }

    /**
     * Возвращает значение константы по псевдониму или <code>null</code>
     * @param alias псевдоним. Может быть <code>null</code>
     * @param clazz класс перечисления
     * @return
     * @throws IllegalArgumentException если <code>clazz</code> равен <code>null</code>
     */
    public static <T extends Enum & EnumAlias> T fromAlias(String alias, Class<T> clazz) {
        Assert.notNull(clazz);
        return fromAlias(alias, clazz, null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum & EnumAlias> T fromAlias(String alias, Class<T> clazz, T defaultValue) {
        if (alias == null) {
            return defaultValue;
        }
        for (EnumAlias v : clazz.getEnumConstants()) {
            if (v.getAlias().equals(alias)) {
                return (T) v;
            }
        }
        return defaultValue;
    }
}
