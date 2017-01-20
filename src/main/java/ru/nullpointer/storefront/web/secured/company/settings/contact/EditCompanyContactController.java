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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.service.ContactService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/settings/contact/edit/{id}")
public class EditCompanyContactController {

    private Logger logger = LoggerFactory.getLogger(EditCompanyContactController.class);
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
    public Contact getContact(@PathVariable("id") Integer id) {
        Contact contact = contactService.getContactById(id);
        if (contact == null) {
            throw new NotFoundException();
        }
        return contact;
    }

    @InitBinder("contact")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("type", "value", "label");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/company/settings/contact/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("contact") Contact contact, BindingResult result, ModelMap model) {
        logger.debug("Contact: {}", contact);

        validator.validate(contact, result);
        if (result.hasErrors()) {
            return "secured/company/settings/contact/edit";
        }

        contactService.updateContactInfo(contact);

        model.clear();
        return "redirect:/secured/company/settings/contact";
    }
}
