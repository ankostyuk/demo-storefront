package ru.nullpointer.storefront.web.support;

import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
public class ActionInfo {

    public enum Type {
        MATCH_ADD_TO_CART,
        MATCH_MOVE_TO_CART,
        MATCH_DELETE_FROM_CART,

        MATCH_ADD_TO_COMPARISON,
        MATCH_DELETE_FROM_COMPARISON,
        
        CART_ADD,
        CART_DELETE,

        COMPARISON_CLEAR
    }
    private Type type;

    private boolean success;
    private String redirect;

    public ActionInfo(Type type) {
        Assert.notNull(type);
        this.type = type;
        this.success = false;
    }

    public Type getType() {
        return type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
}
