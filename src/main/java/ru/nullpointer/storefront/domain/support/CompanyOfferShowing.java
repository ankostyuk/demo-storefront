package ru.nullpointer.storefront.domain.support;

/**
 * Настройки показа предложений поставщика.
 * NOTE: Странное имя выбрано по подобию CompanyOfferSorting.
 * NOTE: Имя Filtering зарезервировано на будущее.
 * @author Alexander Yastrebov
 */
public enum CompanyOfferShowing implements EnumAlias {

    ALL("all"),
    REJECTED("rejected"),
    INACTIVE("inactive");
    //
    private final String alias;

    CompanyOfferShowing(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
