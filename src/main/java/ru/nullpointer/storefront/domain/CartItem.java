package ru.nullpointer.storefront.domain;

import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Описывает элемент списка покупок
 * @author Alexander Yastrebov
 */
public class CartItem {

    private Integer cartId;
    private Date dateAdded;
    private Match match;

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("cartId", cartId)//
                .append("dateAdded", dateAdded)//
                .append("match", match)//
                .toString();
    }
}
