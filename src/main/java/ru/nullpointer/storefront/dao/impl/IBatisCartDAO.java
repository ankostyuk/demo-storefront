package ru.nullpointer.storefront.dao.impl;

import java.util.List;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CartDAO;
import ru.nullpointer.storefront.domain.Cart;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisCartDAO extends SqlMapClientDaoSupport implements CartDAO {

    @Override
    @SuppressWarnings("unchecked")
    public List<Cart> getCartList(Integer sessionId) {
        Assert.notNull(sessionId);
        return getSqlMapClientTemplate().queryForList("Cart.selectBySessionId", sessionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Cart getCart(Integer cartId) {
        Assert.notNull(cartId);
        return (Cart) getSqlMapClientTemplate().queryForObject("Cart.selectById", cartId);
    }

    @Override
    public void insert(Cart cart) {
        Assert.notNull(cart);
        getSqlMapClientTemplate().insert("Cart.insert", cart);
    }

    @Override
    public void updateInfo(Cart cart) {
        Assert.notNull(cart);
        Assert.notNull(cart.getId());
        getSqlMapClientTemplate().update("Cart.updateInfo", cart, 1);
    }

    @Override
    public void delete(Integer cartId) {
        Assert.notNull(cartId);
        getSqlMapClientTemplate().delete("Cart.delete", cartId, 1);
    }
}
