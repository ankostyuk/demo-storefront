package ru.nullpointer.storefront.test.dao;

import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.ContactDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Contact;

/**
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ContactDAOTest {

    @Resource
    private ContactDAO contactDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void test_CRUD() {

        Account acc = DAOTestHelper.getCompanyAccount();

        Contact contact = new Contact();

        contact.setAccountId(acc.getId());
        contact.setType(Contact.Type.PHONE);
        contact.setValue("1234567");
        contact.setLabel(null);

        // CREATE
        contactDAO.insert(contact);
        Integer contactId = contact.getId();
        assertNotNull(contactId);

        // READ
        Contact contact2 = contactDAO.getContactById(contactId);
        assertNotNull(contact2);
        assertContactEquals(contact, contact2);

        // UPDATE
        contact.setValue(contact.getValue() + "UPD");
        contact.setLabel(contact.getLabel() + "многоканальный");

        contactDAO.updateInfo(contact);

        contact2 = contactDAO.getContactById(contactId);
        assertContactEquals(contact, contact2);

        // DELETE
        contactDAO.delete(contactId);
        contact2 = contactDAO.getContactById(contactId);
        assertNull(contact2);
    }

    @Test
    public void test_getAccountContactList() {
        Account acc = DAOTestHelper.getCompanyAccount();
        Integer accountId = acc.getId();

        List<Contact> contactList = contactDAO.getAccountContactList(accountId);
        assertNotNull(contactList);
        assertFalse(contactList.isEmpty());

        for (Contact c : contactList) {
            assertNotNull(c);
            assertEquals(accountId, c.getAccountId());
            assertNotNull(c.getType());
            assertFalse(StringUtils.isBlank(c.getValue()));
        }
    }

    private void assertContactEquals(Contact c1, Contact c2) {
        assertEquals(c1.getId(), c2.getId());
        assertEquals(c1.getAccountId(), c2.getAccountId());
        assertEquals(c1.getType(), c2.getType());
        assertEquals(c1.getValue(), c2.getValue());
        assertEquals(c1.getLabel(), c2.getLabel());
    }
}
