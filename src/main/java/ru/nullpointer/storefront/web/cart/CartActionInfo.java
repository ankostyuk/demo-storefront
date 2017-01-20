package ru.nullpointer.storefront.web.cart;

import ru.nullpointer.storefront.web.support.ActionInfo;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Cart;

/**
 * @author ankostyuk
 */
public class CartActionInfo extends ActionInfo {

    private Cart cart;

    public CartActionInfo(Type type, Cart cart) {
        super(type);
        Assert.notNull(cart);
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }
}
