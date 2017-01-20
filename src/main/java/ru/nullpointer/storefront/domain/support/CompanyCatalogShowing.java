package ru.nullpointer.storefront.domain.support;

/**
 * Настройки показа предложений поставщика.
 * NOTE: Странное имя выбрано по подобию CompanyOfferSorting.
 * NOTE: Имя Filtering зарезервировано на будущее.
 * @author Alexander Yastrebov
 */
public enum CompanyCatalogShowing implements EnumAlias {

    ALL("all"),
    MY("my");
    //
    private final String alias;

    CompanyCatalogShowing(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
