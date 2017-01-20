package ru.nullpointer.storefront.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.SecurityService;

/**
 *
 * @author Alexander Yastrebov
 */
public class AccountAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(AccountAuthenticationProvider.class);
    //
    private RegistrationService registrationService;
    private SecurityService securityService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, "Поддерживается только UsernamePasswordAuthenticationToken");

        String password = getPassword(authentication);
        String email = getEmail(authentication);

        Account account = registrationService.loginIntoAccount(email, password);
        if (account == null) {
            throw new BadCredentialsException("Не удалось совершить вход с логином '" + email + "'");
        }

        AccountDetails details = securityService.getAccountDetails(account);

        return createSuccessAuthentication(details, authentication);
    }

    private String getEmail(Authentication authentication) {
        String email = null;
        if (authentication.getPrincipal() instanceof AccountDetails) {
            email = ((AccountDetails) authentication.getPrincipal()).getEmail();
        } else {
            email = authentication.getName();
        }

        if (email == null || email.trim().isEmpty()) {
            throw new BadCredentialsException("Некорректный адрес электронной почты");
        }

        return email;
    }

    private String getPassword(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            throw new BadCredentialsException("Отсутствует пароль");
        }

        return credentials.toString();
    }

    private Authentication createSuccessAuthentication(AccountDetails details, Authentication authentication) {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(details,
                authentication.getCredentials(), details.getAuthorities());
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<? extends Object> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(registrationService);
        Assert.notNull(securityService);
    }

    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
