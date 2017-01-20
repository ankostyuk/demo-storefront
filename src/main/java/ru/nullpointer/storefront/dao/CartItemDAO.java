package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import ru.nullpointer.storefront.domain.CartItem;

/**
 *
 * @author Alexander Yastrebov
 */
public interface CartItemDAO {

    /**
     * Возвращает карту элементов списка покупок.
     * Ключем карты является идентификатор списка покупок.
     * Значением - список элементов, отсортированный по дате добавления.
     * Карту можно модифицировать.
     * @param sessionId
     * @return
     */
    Map<Integer, List<CartItem>> getCartItemMap(Integer sessionId);

    /**
     * Добавляет элемент в список покупок
     * @param item
     */
    void insert(CartItem item);

    /**
     * Удаляет элемент из списка покупок.
     * Список может не содержать такого элемента - в этом случае возвращется <code>false</code>
     * @param item
     * @return <code>true</code> если такой элемент содержался в списке
     */
    boolean delete(CartItem item);
}
