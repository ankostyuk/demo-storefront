package ru.nullpointer.storefront.web.secured.admin.account;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class AdminAccountHelper {

    private Logger logger = LoggerFactory.getLogger(AdminAccountHelper.class);
    //
    @Resource
    private BeanValidator validator;
    @Resource
    private RegistrationService registrationService;

    public void validateLogin(Login login, BindingResult result, String passwordRepeat, Integer existingAccountId) {
        validator.validate(login, result);

        logger.debug("result: {}", result);

        if (!result.hasFieldErrors("email")) {
            boolean emailChanged = true;
            if (existingAccountId != null) {
                Account account = registrationService.getAccountById(existingAccountId);
                emailChanged = !account.getEmail().equals(login.getEmail());
            }
            // если изменился емейл - проверить доступность
            if (emailChanged && !registrationService.isEmailAvailable(login.getEmail())) {
                result.rejectValue("email", "validation.Login.email.unavailable");
            }
        }

        if (!result.hasFieldErrors("password") && login.getPassword() != null) {
            // проверить повтор пароля
            if (!login.getPassword().equals(passwordRepeat)) {
                result.rejectValue("password", "validation.Login.password.repeat");
            }
        }
    }
}
