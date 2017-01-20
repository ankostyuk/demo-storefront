package ru.nullpointer.storefront.test.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.time.DateUtils;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.AccountSorting;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class AccountDAOTest {

    private Logger logger = LoggerFactory.getLogger(AccountDAOTest.class);
    //
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEmail() {
        accountDAO.getAccountByEmail(null);
        accountDAO.getAccountByEmail(" ");
    }

    @Test
    public void testEmailAvailable() {
        assertFalse(accountDAO.isEmailAvailable("admin@localhost"));
        assertTrue(accountDAO.isEmailAvailable("random_email_address@localhost"));
    }

    @Test
    public void testAdminAccountEmail() {
        Account account = accountDAO.getAccountByEmail("admin@localhost");
        assertNotNull(account);

        assertEquals(Account.Type.ADMIN, account.getType());

        assertEquals("admin@localhost", account.getEmail());

        assertNotNull(account.getPassword());
        assertTrue(!account.getPassword().isEmpty());
    }

    @Test
    public void testAdminAccountId() {
        Account account = accountDAO.getAccountById(0);
        assertNotNull(account);

        assertEquals(Account.Type.ADMIN, account.getType());

        assertEquals("admin@localhost", account.getEmail());

        assertNotNull(account.getPassword());
        assertTrue(!account.getPassword().isEmpty());
    }

    @Test
    public void testInsert() {
        Account account = new Account();

        account.setEmail("test_insert@localhost");
        account.setPassword("test_password");
        account.setType(Account.Type.ADMIN);
        account.setRegistrationDate(new Date());
        
        accountDAO.insert(account);

        Integer id = account.getId();
        assertNotNull(id);

        account = accountDAO.getAccountByEmail("test_insert@localhost");

        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals("test_insert@localhost", account.getEmail());
        assertEquals("test_password", account.getPassword());
    }

    @Test
    public void testInvalidInsert() {
        try {
            accountDAO.insert(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testUpdate() {
        Account account = accountDAO.getAccountByEmail("admin@localhost");

        Integer id = account.getId();

        account.setEmail("test_update@localhost");
        account.setPassword("test_password");

        accountDAO.update(account);

        account = accountDAO.getAccountByEmail("test_update@localhost");

        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals("test_update@localhost", account.getEmail());
        assertEquals("test_password", account.getPassword());
    }

    @Test
    public void testDelete() {
        Account account = new Account();

        account.setEmail("test_delete@localhost");
        account.setPassword("test_password");
        account.setType(Account.Type.ADMIN);
        account.setRegistrationDate(new Date());
        
        accountDAO.insert(account);

        Integer id = account.getId();
        assertNotNull(id);

        accountDAO.delete(id);

        assertNull(accountDAO.getAccountById(id));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUpdate() {
        accountDAO.update(null);
        Account account = new Account();
        account.setId(null);
    }

    @Test
    public void testGetRoleList() {
        Account acc = DAOTestHelper.getManagerAccount();
        List<Role> roleList = accountDAO.getRoleList(acc.getId());

        assertNotNull(roleList);
        assertEquals(1, roleList.size());

        Role r = roleList.get(0);

        assertEquals(Role.ROLE_MANAGER_CATALOG, r);
    }

    @Test
    public void testInsertRole() {
        Account acc = DAOTestHelper.getManagerAccount();
        List<Role> oldRoleList = accountDAO.getRoleList(acc.getId());

        accountDAO.insertRole(acc.getId(), Role.ROLE_MANAGER_DICTIONARY);

        List<Role> newRoleList = accountDAO.getRoleList(acc.getId());
        assertEquals(oldRoleList.size() + 1, newRoleList.size());

        for (Role r : oldRoleList) {
            assertTrue(newRoleList.contains(r));
        }
    }

    @Test
    public void testDeleteRole() {
        Account acc = DAOTestHelper.getManagerAccount();
        List<Role> oldRoleList = accountDAO.getRoleList(acc.getId());

        accountDAO.deleteRole(acc.getId(), Role.ROLE_MANAGER_CATALOG);

        List<Role> newRoleList = accountDAO.getRoleList(acc.getId());
        assertEquals(oldRoleList.size() - 1, newRoleList.size());

        for (Role r : newRoleList) {
            assertTrue(oldRoleList.contains(r));
        }
    }

    @Test
    public void test_getAccountIdListFromRole() {
        List<Integer> list = accountDAO.getAccountIdListFromRole(Role.ROLE_MANAGER_CATALOG);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    public void test_getAccountListByType() {
        List<Account> accountList;

        accountList = accountDAO.getAccountListByType(Account.Type.ADMIN, AccountSorting.EMAIL_ASCENDING);
        checkList(accountList, Account.Type.ADMIN, "email", true);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.EMAIL_ASCENDING);
        checkList(accountList, Account.Type.MANAGER, "email", true);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.EMAIL_DESCENDING);
        checkList(accountList, Account.Type.MANAGER, "email", false);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.DATE_REGISTERED_ASCENDING);
        checkList(accountList, Account.Type.MANAGER, "registrationDate", true);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.DATE_REGISTERED_DESCENDING);
        checkList(accountList, Account.Type.MANAGER, "registrationDate", false);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.DATE_LAST_LOGIN_ASCENDING);
        checkList(accountList, Account.Type.MANAGER, "lastLoginDate", true);

        accountList = accountDAO.getAccountListByType(Account.Type.MANAGER, AccountSorting.DATE_LAST_LOGIN_DESCENDING);
        checkList(accountList, Account.Type.MANAGER, "lastLoginDate", false);
    }

    @Test
    public void test_getAccountMap() {
        Account account1 = accountDAO.getAccountByEmail("company1@example.com");
        Account account2 = accountDAO.getAccountByEmail("company2@example.com");

        Set<Integer> accountIdSet = new HashSet<Integer>();
        accountIdSet.add(account1.getId());
        accountIdSet.add(account2.getId());

        Map<Integer, Account> accountMap = accountDAO.getAccountMap(accountIdSet);

        assertNotNull(accountMap);
        assertEquals(2, accountMap.size());
        assertTrue(accountMap.containsKey(account1.getId()));
        assertTrue(accountMap.containsKey(account2.getId()));
    }

    @Test
    public void test_getRegisteredAccountCount() {
        int count = accountDAO.getRegisteredAccountCount(Account.Type.COMPANY, null, null);
        assertTrue(count > 0);

        Date regDate = DAOTestHelper.getCompanyAccount().getRegistrationDate();
        count = accountDAO.getRegisteredAccountCount(Account.Type.COMPANY, DateUtils.addSeconds(regDate, -1), null);
        assertTrue(count > 0);

        count = accountDAO.getRegisteredAccountCount(Account.Type.COMPANY, null, DateUtils.addSeconds(regDate, 1));
        assertTrue(count > 0);
    }

    private void checkList(List<Account> accountList, Account.Type type, String sortField, boolean ascending) {
        assertNotNull(accountList);
        assertFalse(accountList.isEmpty());

        boolean first = true;
        Account previuos = null;
        for (Account a : accountList) {
            assertNotNull(a);
            assertEquals(type, a.getType());

            if (first) {
                first = false;
            } else {
                try {
                    Comparable prevValue = (Comparable) PropertyUtils.getProperty(previuos, sortField);
                    Comparable value = (Comparable) PropertyUtils.getProperty(a, sortField);
                    if (prevValue != null && value != null) {
                        if ((value instanceof String == false)) {
                            // не сравинивать строки, т.к. сортировка идет на основе локали БД
                            if (ascending) {
                                assertTrue(value.compareTo(prevValue) >= 0);
                            } else {
                                assertTrue(value.compareTo(prevValue) <= 0);
                            }
                        }
                    }
                } catch (Exception ex) {
                    fail();
                }
            }
            previuos = a;
        }
    }
}
