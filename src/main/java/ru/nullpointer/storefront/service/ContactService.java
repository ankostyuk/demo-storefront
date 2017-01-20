package ru.nullpointer.storefront.service;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ContactDAO;
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class ContactService {

    private Logger logger = LoggerFactory.getLogger(ContactService.class);
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private ContactDAO contactDAO;

    @Transactional(readOnly = true)
    public List<Contact> getAccountContactList(Integer accountId) {
        return contactDAO.getAccountContactList(accountId);
    }

    @Transactional(readOnly = true)
    public Contact getContactById(Integer id) {
        return contactDAO.getContactById(id);
    }

    @Secured("ROLE_COMPANY")
    @Transactional
    public void storeContact(Contact contact) {
        checkContact(contact);

        Integer companyId = securityService.getAuthenticatedAccountDetails().getAccountId();
        contact.setAccountId(companyId);

        contactDAO.insert(contact);
    }

    @Secured("ROLE_COMPANY")
    @Transactional
    public void updateContactInfo(Contact contact) {
        checkContact(contact);
        Assert.notNull(getCompanyContactById(contact.getId()), "Попытка изменить несуществующий или чужой контакт");

        contactDAO.updateInfo(contact);
    }

    @Secured("ROLE_COMPANY")
    @Transactional
    public void deleteContact(Contact contact) {
        Assert.notNull(contact);
        Assert.notNull(contact.getId());
        Assert.notNull(getCompanyContactById(contact.getId()), "Попытка удалить несуществующий или чужой контакт");

        contactDAO.delete(contact.getId());
    }

    private void checkContact(Contact contact) {
        Assert.notNull(contact);
        Assert.notNull(contact.getType());

        FieldUtils.nullifyIfEmpty(contact, "label");
    }

    private Contact getCompanyContactById(Integer id) {
        Contact contact = contactDAO.getContactById(id);
        Integer companyId = securityService.getAuthenticatedCompanyId();
        if (contact == null || !contact.getAccountId().equals(companyId)) {
            return null;
        }
        return contact;
    }
}
