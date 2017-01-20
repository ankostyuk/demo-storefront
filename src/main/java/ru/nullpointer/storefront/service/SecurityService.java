package ru.nullpointer.storefront.service;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.security.AccountDetails;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class SecurityService {

    private Logger logger = LoggerFactory.getLogger(SecurityService.class);
    //
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CompanyDAO companyDAO;

    /**
     * Возвращает идентификатор аутентифицированного менеджера
     * @return
     * @throws AuthenticationCredentialsNotFoundException
     *      если текущий пользователь не аутентифицирован
     * @throws BadCredentialsException
     *      если текущий пользователь не является менеджером
     */
    public Integer getAuthenticatedManagerId() {
        AccountDetails details = getAuthenticatedAccountDetails();
        ensureIsManager(details);
        return details.getAccountId();
    }

    /**
     * Возвращает идентификатор аутентифицированного поставщика
     * @return
     * @throws AuthenticationCredentialsNotFoundException
     *      если текущий пользователь не аутентифицирован
     * @throws BadCredentialsException
     *      если текущий пользователь не является поставщиком
     */
    public Integer getAuthenticatedCompanyId() {
        AccountDetails details = getAuthenticatedAccountDetails();
        ensureIsCompany(details);
        return details.getAccountId();
    }

    /**
     * Возвращает аутентифицированного поставщика
     * @return
     * @throws AuthenticationCredentialsNotFoundException
     *      если текущий пользователь не аутентифицирован
     * @throws BadCredentialsException
     *      если текущий пользователь не является поставщиком
     */
    @Transactional(readOnly = true)
    public Company getAuthenticatedCompany() {
        return companyDAO.getCompanyById(getAuthenticatedCompanyId());
    }

    /**
     * Возвращает информацию о текущем аутентифицированном аккаунте
     * @return
     * @throws org.springframework.security.AuthenticationCredentialsNotFoundException
     * если текущий пользователь не аутентифицирован
     */
    public AccountDetails getAuthenticatedAccountDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AuthenticationCredentialsNotFoundException("Объект Authentication равен null");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof AccountDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Пользователь не аутентифицирован (pricipal" + principal + ")");
        }

        return (AccountDetails) principal;
    }

    /**
     * Возвращает список ролей к которым принадлежит аккаунт
     * @param accountId
     * @return список ролей. список может быть пустым если аккаунт не принадлежит
     * ни к одной роли, или аккаунта с таким <code>accountId</code> не существует
     */
    @Transactional(readOnly = true)
    public List<Role> getAccountRoles(Integer accountId) {
        Assert.notNull(accountId);
        return accountDAO.getRoleList(accountId);
    }

    @Transactional(readOnly = true)
    public AccountDetails getAccountDetails(Account account) {
        return new AccountDetails(account, getAuthorities(account));
    }

    /*
     * package private
     */
    void authenticate(Account account) {
        AccountDetails details = new AccountDetails(account, getAuthorities(account));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
    }

    /*
     * private
     */
    private List<GrantedAuthority> getAuthorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        switch (account.getType()) {
            case ADMIN:
                authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN"));
                break;
            case COMPANY:
                authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY"));
                break;
            case MANAGER:
                List<Role> roleList = accountDAO.getRoleList(account.getId());
                for (Role r : roleList) {
                    authorities.add(new GrantedAuthorityImpl(r.name()));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown account type '" + account.getType() + "'");
        }
        return authorities;
    }

    private void ensureIsCompany(AccountDetails details) {
        if (details.getAccountType() != Account.Type.COMPANY) {
            String email = details.getEmail();
            throw new BadCredentialsException("Пользователь с email адресом «" + email + "» не является поставщиком");
        }
    }

    private void ensureIsManager(AccountDetails details) {
        if (details.getAccountType() != Account.Type.MANAGER) {
            String email = details.getEmail();
            throw new BadCredentialsException("Пользователь с email адресом «" + email + "» не является менеджером");
        }
    }
}
