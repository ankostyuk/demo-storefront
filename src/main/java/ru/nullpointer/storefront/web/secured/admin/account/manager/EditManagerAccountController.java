package ru.nullpointer.storefront.web.secured.admin.account.manager;

import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.admin.account.AdminAccountHelper;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/account/manager/edit/{id}")
public class EditManagerAccountController {

    private Logger logger = LoggerFactory.getLogger(EditManagerAccountController.class);
    //
    @Resource
    private RegistrationService registrationService;
    @Resource
    private SecurityService securityService;
    @Resource
    private AdminAccountHelper adminAccountHelper;

    @ModelAttribute("availableRoles")
    public List<Role> getAvailableRoles() {
        return Role.getManagerRoleList();
    }

    @ModelAttribute("accountId")
    public Integer getAccountId(@PathVariable("id") Integer id) {
        return id;
    }

    @ModelAttribute("login")
    public Login getLogin(@PathVariable("id") Integer id) {
        Account account = registrationService.getAccountById(id);
        if (account == null) {
            throw new NotFoundException();
        }
        Login login = new Login();

        login.setEmail(account.getEmail());

        return login;
    }

    @InitBinder("login")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("email", "password");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer id, ModelMap model) {
        model.addAttribute("accountRoleList", securityService.getAccountRoles(id));

        return "secured/admin/account/manager/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer id,
            @ModelAttribute("login") Login login, BindingResult result,
            @RequestParam("passwordRepeat") String passwordRepeat,
            @RequestParam(value = "role", required = false) List<Role> roleList,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        if (roleList == null || roleList.isEmpty()) {
            result.reject("validation.Manager.roles.empty");
        }
        model.addAttribute("accountRoleList", roleList);

        if (StringUtils.isBlank(login.getPassword())) {
            // если пароль пустой - обнулить
            // если пароль обнулен, он не меняется
            login.setPassword(null);
        }

        adminAccountHelper.validateLogin(login, result, passwordRepeat, id);
        if (result.hasErrors()) {
            return "secured/admin/account/manager/edit";
        }

        logger.debug("Login: {}, Role list: {}", login, roleList);

        registrationService.updateManager(id, login, roleList);

        model.clear();
        if (redirect != null) {
            return "redirect:" + redirect;
        }
        return "redirect:/secured/admin/account/manager";
    }
}
