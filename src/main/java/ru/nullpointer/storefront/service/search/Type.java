package ru.nullpointer.storefront.service.search;

import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
// TODO совместить с ru.nullpointer.storefront.domain.Match.Type?
public enum Type {

    MODEL,
    OFFER_PARAM, // TODO использовать?
    OFFER,
    BRAND,
    CATEGORY, // TODO +
    SECTION, // TODO +
    COMPANY; // TODO +

    /**
     * Возвращает порядок типа
     * @param type
     * @return порядок
     */
    // TODO использовать стандартный метод ordinal()
    public static int order(Type type) {
        Assert.notNull(type);
        Type[] values = Type.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(type)) {
                return i;
            }
        }
        return -1;
    }
}
