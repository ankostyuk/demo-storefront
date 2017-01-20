
package ru.nullpointer.storefront.web.secured.admin.account.admin;

import ru.nullpointer.storefront.web.secured.admin.account.manager.*;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/account/admin/delete/{id}")
public class DeleteAdminAccountController {

    @Resource
    private RegistrationService registrationService;

    @ModelAttribute("account")
    public Account getAccount(@PathVariable("id") Integer id) {
        Account account = registrationService.getAccountById(id);
        if (account == null) {
            throw new NotFoundException();
        }
        return account;
    }

    @InitBinder("account")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/admin/account/admin/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer id) {

        registrationService.deleteAdmin(id);

        return "redirect:/secured/admin/account/admin";
    }

}
