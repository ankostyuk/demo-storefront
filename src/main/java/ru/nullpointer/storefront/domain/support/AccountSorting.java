package ru.nullpointer.storefront.domain.support;

/**
 *
 * @author Alexander Yastrebov
 */
public enum AccountSorting implements EnumAlias {

    EMAIL_ASCENDING("email-asc"),
    EMAIL_DESCENDING("email-desc"),
    DATE_REGISTERED_ASCENDING("date-registered-asc"),
    DATE_REGISTERED_DESCENDING("date-registered-desc"),
    DATE_LAST_LOGIN_ASCENDING("date-last-login-asc"),
    DATE_LAST_LOGIN_DESCENDING("date-last-login-desc");
    //
    private final String alias;

    AccountSorting(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
}
