package ru.nullpointer.storefront.web.secured.company.settings.contact;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.service.ContactService;
import ru.nullpointer.storefront.service.SecurityService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class CompanyContactController {

    @Resource
    private SecurityService securityService;
    @Resource
    private ContactService contactService;

    @RequestMapping(value = "/secured/company/settings/contact", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        List<Contact> contactList = contactService.getAccountContactList(companyId);
        model.addAttribute("contactList", contactList);
    }
}
