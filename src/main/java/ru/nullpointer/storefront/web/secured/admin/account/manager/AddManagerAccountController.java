package ru.nullpointer.storefront.web.secured.admin.account.manager;

import java.util.List;
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
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.secured.admin.account.AdminAccountHelper;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/account/manager/add")
public class AddManagerAccountController {

    private Logger logger = LoggerFactory.getLogger(AddManagerAccountController.class);
    //
    @Resource
    private RegistrationService registrationService;
    @Resource
    private AdminAccountHelper adminAccountHelper;

    @ModelAttribute("availableRoles")
    public List<Role> getAvailableRoles() {
        return Role.getManagerRoleList();
    }

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
            @RequestParam(value = "role", required = false) List<Role> roleList,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        if (roleList == null || roleList.isEmpty()) {
            result.reject("validation.Manager.roles.empty");
        }
        model.addAttribute("accountRoleList", roleList);

        adminAccountHelper.validateLogin(login, result, passwordRepeat, null);
        if (result.hasErrors()) {
            return "secured/admin/account/manager/add";
        }

        logger.debug("Login: {}, Role list: {}", login, roleList);

        registrationService.createManager(login, roleList);

        model.clear();
        if (redirect != null) {
            return "redirect:" + redirect;
        }
        return "redirect:/secured/admin/account/manager";
    }
}
