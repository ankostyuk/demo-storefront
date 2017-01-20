package ru.nullpointer.storefront.web.secured;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.support.BuildModeSupport;
import ru.nullpointer.storefront.web.ui.settings.PasswordUpdate;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class PasswordUpdateController {

    private Logger logger = LoggerFactory.getLogger(PasswordUpdateController.class);
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private RegistrationService registrationService;
    @Autowired
    private BeanValidator validator;
    @Resource
    private BuildModeSupport buildModeSupport;

    @ModelAttribute("passwordUpdate")
    public PasswordUpdate getPasswordUpdate() {
        return new PasswordUpdate();
    }

    @RequestMapping(value = "/secured/company/settings/password", method = RequestMethod.GET)
    public void handleCompanyGet() {
        buildModeSupport.notFoundInDemo();
    }

    @RequestMapping(value = "/secured/company/settings/password", method = RequestMethod.POST)
    public String handleCompanyPost(@ModelAttribute("passwordUpdate") PasswordUpdate passwordUpdate, Errors errors) {
        if (handlePost(passwordUpdate, errors)) {
            return "redirect:/secured/company/settings/password?updated";
        } else {
            return "secured/company/settings/password";
        }
    }

    private boolean handlePost(PasswordUpdate passwordUpdate, Errors errors) {
        buildModeSupport.notFoundInDemo();

        String email = securityService.getAuthenticatedAccountDetails().getEmail();

        validator.validate(passwordUpdate, errors);

        String oldPassword = passwordUpdate.getOldPassword();
        if (oldPassword.isEmpty()) {
            errors.rejectValue("oldPassword", "validation.PasswordUpdate.oldPassword.empty");
        } else if (!registrationService.isPasswordValid(email, oldPassword)) {
            errors.rejectValue("oldPassword", "validation.PasswordUpdate.oldPassword.incorrect");
        }

        if (!errors.hasFieldErrors("newPassword")) {
            String newPassword = passwordUpdate.getNewPassword();
            String newPasswordRepeat = passwordUpdate.getNewPasswordRepeat();
            if (!newPassword.equals(newPasswordRepeat)) {
                errors.rejectValue("newPassword", "validation.PasswordUpdate.newPassword.repeat");
            }
        }

        if (errors.hasErrors()) {
            return false;
        }

        registrationService.updatePassword(email, passwordUpdate.getNewPassword());

        logger.debug("Пароль для аккаунта «{}» обновлен", email);

        return true;
    }
}
