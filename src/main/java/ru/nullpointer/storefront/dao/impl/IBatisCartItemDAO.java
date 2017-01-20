package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CartItemDAO;
import ru.nullpointer.storefront.domain.CartItem;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisCartItemDAO extends SqlMapClientDaoSupport implements CartItemDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<CartItem>> getCartItemMap(Integer sessionId) {
        Assert.notNull(sessionId);

        List<CartItem> itemList = getSqlMapClientTemplate().queryForList("CartItem.selectBySessionId", sessionId);
        if (itemList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, List<CartItem>> result = new HashMap<Integer, List<CartItem>>();
        // Список отсортирован сначала по cartId, затем по dateAdded
        for (CartItem item : itemList) {
            Integer cartId = item.getCartId();
            List<CartItem> list = result.get(cartId);
            if (list == null) {
                list = new ArrayList<CartItem>();
            }
            list.add(item);
            result.put(cartId, list);
        }

        return result;
    }

    @Override
    public void insert(CartItem item) {
        Assert.notNull(item);
        getSqlMapClientTemplate().insert("CartItem.insert", item);
    }

    @Override
    public boolean delete(CartItem item) {
        Assert.notNull(item);
        return getSqlMapClientTemplate().delete("CartItem.delete", item) > 0;
    }
}
