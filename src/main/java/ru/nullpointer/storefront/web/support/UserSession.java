package ru.nullpointer.storefront.web.support;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.CartItem;
import ru.nullpointer.storefront.domain.Settings;

/**
 *
 * @author Alexander Yastrebov
 */
public class UserSession {

    private Settings settings;
    private Map<Integer, List<Match>> comparisonMap;
    private List<Cart> cartList;
    private Map<Integer, List<CartItem>> cartItemMap;

    public UserSession(Settings settings, Map<Integer, List<Match>> comparisonMap,
            List<Cart> cartList, Map<Integer, List<CartItem>> cartItemMap) {
        Assert.notNull(settings);
        Assert.notNull(comparisonMap);
        Assert.notNull(cartList);
        Assert.notNull(cartItemMap);

        this.settings = settings;
        this.comparisonMap = comparisonMap;
        this.cartList = cartList;
        this.cartItemMap = cartItemMap;
    }

    public Settings getSettings() {
        return settings;
    }

    public Map<Integer, List<Match>> getComparisonMap() {
        return comparisonMap;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public Map<Integer, List<CartItem>> getCartItemMap() {
        return cartItemMap;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("settings", settings)//
                .append("comparisonMap", comparisonMap)//
                .append("cartList", cartList)//
                .append("cartItemMap", cartItemMap)//
                .toString();
    }
}
