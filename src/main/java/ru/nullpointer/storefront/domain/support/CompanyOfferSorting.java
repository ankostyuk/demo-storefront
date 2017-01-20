package ru.nullpointer.storefront.domain.support;

/**
 *
 * @author Alexander Yastrebov
 */
public enum CompanyOfferSorting implements EnumAlias {

    NAME("name"),
    DATE_CREATED_ASCENDING("date-created-asc"),
    DATE_CREATED_DESCENDING("date-created-desc"),
    DATE_EDITED_ASCENDING("date-edited-asc"),
    DATE_EDITED_DESCENDING("date-edited-desc");
    //
    private final String alias;

    CompanyOfferSorting(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
