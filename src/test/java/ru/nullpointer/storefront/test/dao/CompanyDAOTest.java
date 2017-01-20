package ru.nullpointer.storefront.test.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
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
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CompanyDAOTest {

    private Logger logger = LoggerFactory.getLogger(CompanyDAOTest.class);
    //
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void testInvalidAccountId() {
        Company company = companyDAO.getCompanyById(-100);
        assertNull(company);
    }

    @Test
    public void testCompanyAccount() {
        Account account = accountDAO.getAccountByEmail("company1@example.com");
        assertNotNull(account);

        Company company = companyDAO.getCompanyById(account.getId());
        assertNotNull(company);

        assertNotNull(company.getName());
        assertFalse(company.getName().trim().isEmpty());

        assertNotNull(company.getRegionId());
    }

    @Test
    public void testInsert() {
        Account account = new Account();

        account.setEmail("test_email@example.org");
        account.setPassword("test_password");
        account.setType(Account.Type.COMPANY);
        account.setRegistrationDate(new Date());

        accountDAO.insert(account);

        Integer accountId = account.getId();

        Company company = new Company();
        assertNotNull(company.getPaymentConditions());

        company.setId(accountId);

        Region region = DAOTestHelper.getRegionByName("Москва");
        assertNotNull(region);

        company.setRegionId(region.getId());
        company.setName("test_insert");
        company.setAddress("address");
        company.setContactPhone("123");

        company.getPaymentConditions().setText("payment_text");
        company.getPaymentConditions().setCash(true);
        company.getPaymentConditions().setCashless(false);

        companyDAO.insert(company);

        company = companyDAO.getCompanyById(accountId);
        assertNotNull(company);
        assertNotNull(company.getId());
        assertNotNull(company.getPaymentConditions());

        assertEquals("test_insert", company.getName());
        assertEquals("address", company.getAddress());
        assertEquals("123", company.getContactPhone());

        assertEquals("payment_text", company.getPaymentConditions().getText());
        assertEquals(true, company.getPaymentConditions().getCash());
        assertEquals(false, company.getPaymentConditions().getCashless());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInsert() {
        companyDAO.insert(null);

        Company company = new Company();
        company.setId(null);
        companyDAO.insert(company);
    }

    @Test
    public void testUpdate() {
        Account account = accountDAO.getAccountByEmail("company1@example.com");
        Integer accountId = account.getId();

        Company company = companyDAO.getCompanyById(accountId);
        company.setName("test_update");
        company.setAddress("address_update");
        company.setContactPhone("321");

        companyDAO.update(company);

        company = companyDAO.getCompanyById(accountId);
        assertNotNull(company);
        assertEquals("test_update", company.getName());
        assertEquals("address_update", company.getAddress());
        assertEquals("321", company.getContactPhone());
    }

    @Test
    public void testUpdateInfo() {
        Account account = accountDAO.getAccountByEmail("company1@example.com");
        Integer accountId = account.getId();

        Company company = companyDAO.getCompanyById(accountId);
        company.setName("test_update");
        company.setAddress("address_update");
        company.setContactPhone("321");

        companyDAO.updateInfo(company);

        company = companyDAO.getCompanyById(accountId);
        assertNotNull(company);

        // проверить что имя не изменилось
        // TODO Подумать об обновлении <code>name</code>
        // assertEquals("company1", company.getName());

        assertEquals("address_update", company.getAddress());
        assertEquals("321", company.getContactPhone());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUpdate() {
        companyDAO.update(null);

        Company company = new Company();
        company.setId(null);
        companyDAO.update(company);
    }

    @Test
    public void testCompanyMap() {
        Account account = accountDAO.getAccountByEmail("company1@example.com");
        Integer accountId = account.getId();

        Company company = companyDAO.getCompanyById(accountId);

        Set<Integer> companyIdSet = new HashSet<Integer>();
        companyIdSet.add(company.getId());

        Map<Integer, Company> companyMap = companyDAO.getCompanyMap(companyIdSet);

        assertNotNull(companyMap);
        assertTrue(companyMap.size() == 1);

        Company c = companyMap.get(company.getId());
        assertNotNull(c);
        assertEquals(company.getId(), c.getId());
    }

    @Test
    public void testInvalidCompanyMap() {
        try {
            companyDAO.getCompanyMap(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        Set<Integer> companyIdSet = new HashSet<Integer>();

        // пустой список
        Map<Integer, Company> companyMap = companyDAO.getCompanyMap(companyIdSet);
        assertTrue(companyMap.isEmpty());

        // кривые данные
        companyIdSet.add(-100);
        companyMap = companyDAO.getCompanyMap(companyIdSet);
        assertTrue(companyMap.isEmpty());
    }

    @Test
    public void test_getCompanyList() {
        PageConfig pageConfig = new PageConfig(1, 10);

        List<Company> companyList = companyDAO.getCompanyList(null, pageConfig);
        assertNotNull(companyList);
        assertFalse(companyList.isEmpty());
        assertTrue(companyList.size() <= pageConfig.getPageSize());

        logger.debug("companyList: {}", companyList);
        Date emailAuthDate = null;
        for (Company company : companyList) {
            Account account = accountDAO.getAccountById(company.getId());
            logger.debug("company account email auth date: {}", account.getEmailAuthenticatedDate());

            if (account.getEmailAuthenticatedDate() == null) {
                assertNull(emailAuthDate);
            } else {
                if (emailAuthDate == null) {
                    emailAuthDate = account.getEmailAuthenticatedDate();
                } else {
                    assertTrue(account.getEmailAuthenticatedDate().getTime() <= emailAuthDate.getTime());
                }
            }
        }

        companyList = companyDAO.getCompanyList("company1", pageConfig);
        assertNotNull(companyList);
        assertEquals(1, companyList.size());

        companyList = companyDAO.getCompanyList("unknown_company", pageConfig);
        assertNotNull(companyList);
        assertTrue(companyList.isEmpty());
    }

    @Test
    public void test_getCompanyCount() {
        int count = companyDAO.getCompanyCount(null);
        assertTrue(count > 0);

        count = companyDAO.getCompanyCount("company1");
        assertEquals(1, count);
    }
}
