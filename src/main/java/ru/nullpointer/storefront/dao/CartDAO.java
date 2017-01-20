package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.Cart;

/**
 *
 * @author Alexander Yastrebov
 */
public interface CartDAO {

    /**
     * Возвращает список списков покупок для заданной сессии,
     * отсортированный в порядке добавления.
     * @param sessionId
     * @return
     */
    List<Cart> getCartList(Integer sessionId);

    /**
     * Возвращает список покупок по идентификационному номеру или <code>null</code>
     * @param cartId
     * @return
     */
    Cart getCart(Integer cartId);

    /**
     * Добавляет список покупок
     * @param cart
     */
    void insert(Cart cart);

    /**
     * Обновляет наименование и описание списка покупок.
     * @param cart
     */
    void updateInfo(Cart cart);

    /**
     * Удаляет список покупок
     * @param cartId
     */
    void delete(Integer cartId);
}
