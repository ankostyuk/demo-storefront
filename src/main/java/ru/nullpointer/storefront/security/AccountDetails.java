package ru.nullpointer.storefront.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Account.Type;

/**
 *
 * @author Alexander Yastrebov
 */
public final class AccountDetails implements Serializable {

    private static final long serialVersionUID = 2L;
    //
    private Integer accountId;
    private String email;
    private Account.Type accountType;
    private List<GrantedAuthority> authorities;

    public AccountDetails(Account account, List<GrantedAuthority> authorities) {
        Assert.notNull(account);
        Assert.notNull(account.getId());
        Assert.notNull(account.getEmail());
        Assert.notNull(account.getType());

        this.accountId = account.getId();
        this.email = account.getEmail();
        this.accountType = account.getType();
        this.authorities = Collections.unmodifiableList(authorities);
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public Type getAccountType() {
        return accountType;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
