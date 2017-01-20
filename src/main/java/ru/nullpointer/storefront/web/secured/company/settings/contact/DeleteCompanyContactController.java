package ru.nullpointer.storefront.web.secured.company.settings.contact;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.service.ContactService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/settings/contact/delete/{id}")
public class DeleteCompanyContactController {

    @Resource
    private ContactService contactService;

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
        binder.setDisallowedFields("id");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/company/settings/contact/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("contact") Contact contact) {

        contactService.deleteContact(contact);

        return "redirect:/secured/company/settings/contact";
    }
}
