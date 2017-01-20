package ru.nullpointer.storefront.dao.impl;

import java.util.List;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ContactDAO;
import ru.nullpointer.storefront.domain.Contact;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisContactDAO extends SqlMapClientDaoSupport implements ContactDAO {

    @Override
    public List<Contact> getAccountContactList(Integer accountId) {
        Assert.notNull(accountId);
        return (List<Contact>) getSqlMapClientTemplate().queryForList("Contact.selectByAccountId", accountId);
    }

    @Override
    public Contact getContactById(Integer id) {
        Assert.notNull(id);
        return (Contact) getSqlMapClientTemplate().queryForObject("Contact.selectById", id);
    }

    @Override
    public void insert(Contact contact) {
        Assert.notNull(contact);
        getSqlMapClientTemplate().insert("Contact.insert", contact);
    }

    @Override
    public void updateInfo(Contact contact) {
        Assert.notNull(contact);
        Assert.notNull(contact.getId());
        getSqlMapClientTemplate().update("Contact.updateInfo", contact, 1);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Contact.delete", id, 1);
    }
}
