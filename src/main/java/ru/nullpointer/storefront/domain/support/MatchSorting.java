package ru.nullpointer.storefront.domain.support;

/**
 *
 * @author Alexander Yastrebov
 */
public enum MatchSorting implements EnumAlias {

    PRICE_ASCENDING("price-asc"),
    PRICE_DESCENDING("price-desc");
    //
    private final String alias;

    MatchSorting(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
