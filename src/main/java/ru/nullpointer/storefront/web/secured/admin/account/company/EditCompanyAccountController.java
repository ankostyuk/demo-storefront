package ru.nullpointer.storefront.web.secured.admin.account.company;

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
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.admin.account.AdminAccountHelper;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/account/company/edit/{id}")
public class EditCompanyAccountController {

    private Logger logger = LoggerFactory.getLogger(EditCompanyAccountController.class);
    //
    @Resource
    private CompanyService companyService;
    @Resource
    private RegistrationService registrationService;
    @Resource
    private AdminAccountHelper adminAccountHelper;
    @Resource
    private BeanValidator validator;

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

    @ModelAttribute("company")
    public Company getCompany(@PathVariable("id") Integer id) {
        Company company = companyService.getCompanyById(id);
        if (company == null) {
            throw new NotFoundException();
        }
        return company;
    }

    @InitBinder("login")
    public void initLoginBinder(WebDataBinder binder) {
        binder.setAllowedFields("email", "password");
    }

    @InitBinder("company")
    public void initCompanyBinder(WebDataBinder binder) {
        binder.setAllowedFields("name");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/admin/account/company/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer id,
            @ModelAttribute("login") Login login, BindingResult loginResult,
            @ModelAttribute("company") Company company, BindingResult companyResult,
            @RequestParam("passwordRepeat") String passwordRepeat,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        if (StringUtils.isBlank(login.getPassword())) {
            // если пароль пустой - обнулить
            // если пароль обнулен, он не меняется
            login.setPassword(null);
        }

        adminAccountHelper.validateLogin(login, loginResult, passwordRepeat, id);
        validator.validate(company, companyResult);

        if (loginResult.hasErrors() || companyResult.hasErrors()) {
            return "secured/admin/account/company/edit";
        }

        logger.debug("Login: {}, Company: {}", new Object[]{login, company});
        
        registrationService.updateCompany(id, login, company);

        model.clear();
        if (redirect != null) {
            return "redirect:" + redirect;
        }
        return "redirect:/secured/admin/account/company";
    }
}
