package ru.nullpointer.storefront.web.secured.company.settings.contact;

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
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.service.ContactService;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/settings/contact/add")
public class AddCompanyContactController {

    private Logger logger = LoggerFactory.getLogger(AddCompanyContactController.class);
    //
    @Resource
    private ContactService contactService;
    //
    @Resource
    private BeanValidator validator;

    @ModelAttribute("contactTypeValues")
    public Contact.Type[] getContactTypeValues() {
        return Contact.Type.values();
    }

    @ModelAttribute("contact")
    public Contact getContact() {
        Contact contact = new Contact();
        return contact;
    }

    @InitBinder("contact")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("type", "value", "label");
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet() {
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("contact") Contact contact, BindingResult result, ModelMap model) {
        logger.debug("Contact: {}", contact);

        validator.validate(contact, result);
        if (result.hasErrors()) {
            return "secured/company/settings/contact/add";
        }

        contactService.storeContact(contact);

        model.clear();
        return "redirect:/secured/company/settings/contact";
    }
}
