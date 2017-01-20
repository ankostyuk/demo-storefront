package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.Contact;

/**
 *
 * @author Alexander Yastrebov
 */
public interface ContactDAO {

    List<Contact> getAccountContactList(Integer accountId);

    Contact getContactById(Integer id);

    void insert(Contact contact);

    void updateInfo(Contact contact);

    void delete(Integer id);
}
