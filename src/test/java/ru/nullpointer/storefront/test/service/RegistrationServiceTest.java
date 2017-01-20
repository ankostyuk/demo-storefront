package ru.nullpointer.storefront.test.service;

import java.util.List;
import javax.annotation.Resource;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.RegistrationService;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class RegistrationServiceTest {

    private static final String ADMIN_EMAIL = "admin@localhost";
    private static final String ADMIN_PASSWORD = "admin";
    //
    @Resource
    private RegistrationService regService;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private RegionDAO regionDAO;

    @Test
    public void testEmailAvailable() {
        assertFalse(regService.isEmailAvailable(ADMIN_EMAIL));
        assertFalse(regService.isEmailAvailable(ADMIN_EMAIL.toUpperCase()));
        assertFalse(regService.isEmailAvailable(ADMIN_EMAIL + "    "));
        assertFalse(regService.isEmailAvailable(" " + ADMIN_EMAIL));

        assertTrue(regService.isEmailAvailable("random_email_address@localhost"));
    }

    @Test
    public void testLogin() {
        assertNotNull(regService.loginIntoAccount(ADMIN_EMAIL, ADMIN_PASSWORD));
        assertNotNull(regService.loginIntoAccount("manager1@example.com", "manager1"));

        assertNotNull(regService.loginIntoAccount(ADMIN_EMAIL.toUpperCase(), ADMIN_PASSWORD));
        assertNotNull(regService.loginIntoAccount("    " + ADMIN_EMAIL, ADMIN_PASSWORD));
        assertNotNull(regService.loginIntoAccount(ADMIN_EMAIL + " ", ADMIN_PASSWORD));
    }

    @Test
    public void testInvalidLogin() {
        assertNull(regService.loginIntoAccount("invalid_email@example.org", "123"));
    }

    @Test
    public void testEmailNotConfirmedLogin() {
        assertNull(regService.loginIntoAccount("company4@example.com", "company4"));
    }

    @Test
    public void testCompanyRegistration() {
        registerCompany();
    }

    @Test
    public void testEmailConfirmation() {
        Company company = registerCompany();

        Account account = company.getAccount();
        assertTrue(regService.confirmEmail(account.getEmail(), account.getEmailToken()));

        account = accountDAO.getAccountByEmail("test_registration@example.com");
        assertNotNull(account.getEmailAuthenticatedDate());
    }

    @Test
    public void testInvalidEmailConfirmation() {
        Company company = registerCompany();

        Account account = company.getAccount();
        assertFalse(regService.confirmEmail("wrong_email@example.org", "wrong_token"));
        assertFalse(regService.confirmEmail("wrong_email@example.org", account.getEmailToken()));
        assertFalse(regService.confirmEmail(account.getEmail(), "wrong_token"));

        account = accountDAO.getAccountByEmail("test_registration@example.com");
        assertNull(account.getEmailAuthenticatedDate());
    }

    @Test
    public void testRemindPassword() {
        Company company = registerCompany();

        Account account = company.getAccount();
        String email = account.getEmail();

        regService.confirmEmail(email, account.getEmailToken());

        assertTrue(regService.remindPassword(email));

        account = accountDAO.getAccountByEmail(email);
        assertNotNull(account.getNewPassword());
        assertNotNull(account.getNewPasswordDate());
    }

    @Test
    public void testInvalidRemindPassword() {
        Company company = registerCompany();

        Account account = company.getAccount();
        String email = account.getEmail();

        // Не подтвержденный емайл -> пароль не высылать
        assertFalse(regService.remindPassword(email));

        account = accountDAO.getAccountByEmail(email);
        assertNull(account.getNewPassword());
        assertNull(account.getNewPasswordDate());
    }

    @Test
    public void testIsPasswordValid() {
        assertTrue(regService.isPasswordValid(ADMIN_EMAIL, ADMIN_PASSWORD));
        assertTrue(!regService.isPasswordValid(ADMIN_EMAIL, "bad password"));
    }

    @Test
    public void testUpdatePassword() {
        final String newPassword = "newPassword!";
        assertTrue(regService.updatePassword(ADMIN_EMAIL, newPassword));
        assertTrue(regService.isPasswordValid(ADMIN_EMAIL, newPassword));
    }

    private Company registerCompany() {
        Account acc = new Account();

        // проверка на регистр  и пробелы
        acc.setEmail("   test_registration@EXAMPLE.COM      ");
        acc.setPassword("reg_pass");

        Company com = new Company();
        com.setAccount(acc);
        com.setName("reg_test");

        List<Region> regionList = regionDAO.getRegionsByNameText("Москва");
        assertFalse(regionList.isEmpty());
        Region region = regionList.get(0);

        com.setRegionId(region.getId());

        com.setAddress("reg_address");
        com.setContactPhone("123321");

        regService.registerCompany(com);

        assertNotNull(acc.getId());
        assertNotNull(com.getId());
        assertNotNull(com.getAccount());

        assertEquals(Account.Type.COMPANY, acc.getType());

        assertEquals("test_registration@example.com", acc.getEmail());

        assertNotNull(acc.getPassword());

        assertEquals("reg_test", com.getName());
        assertEquals("reg_address", com.getAddress());
        assertEquals("123321", com.getContactPhone());

        assertNull(acc.getNewPassword());
        assertNull(acc.getNewPasswordDate());

        assertNotNull(acc.getRegistrationDate());

        assertNull(acc.getEmailAuthenticatedDate());

        assertNotNull(acc.getEmailToken());
        assertTrue(acc.getEmailToken().length() > 0);

        assertNotNull(acc.getEmailTokenExpiresDate());
        assertTrue(acc.getEmailTokenExpiresDate().getTime() > acc.getRegistrationDate().getTime());

        return com;
    }
}
