package ru.nullpointer.storefront.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.support.ResultGroup;

/**
 * Группирует результаты по первой букве свойства <code>groupValue</code>
 * Результаты начинающиеся с цифры попадают в одну группу с именем заданным
 * константой <code>DIGIT_GROUP</code>
 *
 * Не учитывает регистр.
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public abstract class AlphabetResultBuilder<T> implements GroupedQueryResultBuilder<T> {

    public static final String DIGIT_GROUP = "0";
    //
    private Map<String, List<T>> resultListMap;
    private Comparator<T> groupComparator;
    private Comparator<String> tocComparator;

    public AlphabetResultBuilder() {
        tocComparator = String.CASE_INSENSITIVE_ORDER;

        resultListMap = new TreeMap<String, List<T>>(tocComparator);
        groupComparator = new GroupComparator();
    }

    @Override
    public List<String> buildToc(List<String> itemList) {
        Set<String> set = new TreeSet<String>(tocComparator);
        for (String item : itemList) {
            set.add(getPrefix(item));
        }
        return new ArrayList<String>(set);
    }

    @Override
    public List<ResultGroup<T>> buildResult(List<T> itemList) {
        for (T item : itemList) {
            addItem(item);
        }

        List<ResultGroup<T>> groupList = new ArrayList<ResultGroup<T>>();
        for (String key : resultListMap.keySet()) {
            List<T> list = resultListMap.get(key);
            Collections.sort(list, groupComparator);
            groupList.add(new ResultGroup<T>(key, list));
        }
        return groupList;
    }

    private void addItem(T result) {
        String groupValue = getGroupValue(result);
        String prefix = getPrefix(groupValue);

        if (!resultListMap.containsKey(prefix)) {
            ArrayList<T> list = new ArrayList<T>();
            list.add(result);
            resultListMap.put(prefix, list);
        } else {
            resultListMap.get(prefix).add(result);
        }
    }

    private String getPrefix(String s) {
        Assert.hasText(s);

        String first = s.substring(0, 1);
        if (Character.isDigit(first.charAt(0))) {
            return DIGIT_GROUP;
        } else {
            return first.toUpperCase();
        }
    }

    private class GroupComparator implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            String s1 = getGroupValue(o1);
            String s2 = getGroupValue(o2);

            return s1.compareToIgnoreCase(s2);
        }
    }
}
