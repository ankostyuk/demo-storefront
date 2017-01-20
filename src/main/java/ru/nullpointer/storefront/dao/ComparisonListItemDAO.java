package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import ru.nullpointer.storefront.domain.ComparisonListItem;
import ru.nullpointer.storefront.domain.Match;

/**
 *
 * @author Alexander Yastrebov
 */
public interface ComparisonListItemDAO {

    /**
     * Возвращает карту списков сравнения.
     * Ключем карты является идентификатор категории,
     * значением - список элементов сравнения отсортированный в порядке добавления.
     * Карту можно модифицировать.
     * @param sessionId
     * @return
     */
    Map<Integer, List<Match>> getComparisonMap(Integer sessionId);

    /**
     * Добавляет элемент к сравнению
     * @param item
     */
    void insert(ComparisonListItem item);

    /**
     * Убирает элемент из сравнения.
     * Элемент может отсутствовать в списке сравнения
     * @param item
     */
    void delete(ComparisonListItem item);

    /**
     * Убирает все элементы из списка для данной категории
     * @param sessionId
     * @param categoryId
     */
    void deleteAll(Integer sessionId, Integer categoryId);
}
