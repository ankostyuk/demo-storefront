package ru.nullpointer.storefront.web.secured.admin.account.admin;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.secured.admin.account.AdminAccountHelper;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/account/admin/add")
public class AddAdminAccountController {

    private Logger logger = LoggerFactory.getLogger(AddAdminAccountController.class);
    //
    @Resource
    private RegistrationService registrationService;
    @Resource
    private AdminAccountHelper adminAccountHelper;

    @ModelAttribute("login")
    public Login getLogin() {
        Login login = new Login();
        return login;
    }

    @InitBinder("login")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("email", "password");
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet() {
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("login") Login login, BindingResult result,
            @RequestParam("passwordRepeat") String passwordRepeat,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        adminAccountHelper.validateLogin(login, result, passwordRepeat, null);
        if (result.hasErrors()) {
            return "secured/admin/account/admin/add";
        }

        registrationService.createAdmin(login);

        model.clear();
        if (redirect != null) {
            return "redirect:" + redirect;
        }
        return "redirect:/secured/admin/account/admin";
    }
}
