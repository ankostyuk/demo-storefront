package ru.nullpointer.storefront.web.support;

import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
public class MatchActionInfo extends ActionInfo {

    private String[] idValues;

    public MatchActionInfo(Type type, String[] idValues) {
        super(type);
        Assert.notNull(idValues);
        this.idValues = idValues;
    }

    public String[] getIdValues() {
        return idValues;
    }
}
