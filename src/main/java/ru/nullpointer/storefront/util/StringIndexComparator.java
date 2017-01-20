package ru.nullpointer.storefront.util;

import java.util.Comparator;

/**
 * @author ankostyuk
 */
public class StringIndexComparator implements Comparator<Object> {
    @Override
    public int compare(Object str1, Object str2) {
        try {
            return (Integer.parseInt((String)str1) - Integer.parseInt((String)str2));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
