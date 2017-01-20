package ru.nullpointer.storefront.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.AccountSorting;
import ru.nullpointer.storefront.domain.support.Login;
import ru.nullpointer.storefront.util.RandomUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class RegistrationService {

    private Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    //
    private static final int PASSWORD_REMIND_INTERVAL_MINUTES = 10;
    private static final int EMAIL_CONFIRM_INTERVAL_DAYS = 3;
    private static final int EMAIL_TOKEN_LENGTH = 10;
    private static final int PASSWORD_LENGTH = 10;
    //
    @Value("${mail.from}")
    private String mailFromAddress;
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private TimeService timeService;
    @Resource
    private EmailService emailService;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private OfferModerationHelper offerModerationHelper;
    //
    @Resource
    private MessageSource messageSource;
    //
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder = new PlaintextPasswordEncoder();

    @Secured("ROLE_ADMIN")
    @Transactional(readOnly = true)
    public Account getAccountById(Integer accountId) {
        return accountDAO.getAccountById(accountId);
    }

    /**
     * Проверяет доступность адреса электронной почты для регистрации аккаунта
     * @param email
     * @return <code>false</code> если существует аккаунт зарегистрированный на этот адрес
     * @throws IllegalArgumentException если <code>email</code> пустая строка или <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        Assert.hasText(email);
        return accountDAO.isEmailAvailable(normalizeEmail(email));
    }

    /**
     * Выполняет проверку возможности входа для данной пары емайл адреса и пароля
     * @param email емайл адрес
     * @param password пароль
     * @return <code>account</code> соответствующий паре емайл/пароль или <code>null</code> если вход невозможен
     * @throws IllegalArgumentException если <code>email</code> пустая строка или <code>null</code>
     */
    @Transactional
    public Account loginIntoAccount(String email, String password) {
        Account account = getAccountByEmail(email);

        if (account == null) {
            return null;
        }

        // Email поставщика должен быть подтвержден
        if (account.getType() == Account.Type.COMPANY && account.getEmailAuthenticatedDate() == null) {
            return null;
        }

        Object salt = account.getId();

        // пароль не совпадает
        if (!passwordEncoder.isPasswordValid(account.getPassword(), password, salt)) {
            // проверить новый пароль
            String newPassword = account.getNewPassword();
            if (newPassword != null && passwordEncoder.isPasswordValid(newPassword, password, salt)) {
                logger.debug("Account with email {} logged in with new password", account.getEmail());
                // обновить пароль
                account.setPassword(newPassword);
                account.setNewPassword(null);
            } else {
                // оба пароля не подходят
                return null;
            }
        }

        account.setLastLoginDate(timeService.now());
        accountDAO.update(account);

        return account;
    }

    @Transactional
    public void registerCompany(Company company) {
        Assert.notNull(company);
        checkCompany(company);

        Account account = company.getAccount();
        checkAccount(account);

        String plainPassword = account.getPassword();

        account.setType(Account.Type.COMPANY);
        createAccount(account);

        prepareCompany(company);

        company.setId(account.getId());
        companyDAO.insert(company);

        sendCompanyRegistrationEmail(account.getEmail(), plainPassword, account.getEmailToken());
    }

    /**
     * Регистрирует менеджера
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void createManager(Login login, List<Role> roleList) {
        Assert.notNull(login);
        Assert.hasText(login.getEmail());
        Assert.hasText(login.getPassword());

        Account account = new Account();

        account.setEmail(login.getEmail());
        account.setPassword(login.getPassword());
        account.setType(Account.Type.MANAGER);

        createAccount(account);

        setManagerRoles(account.getId(), roleList);
    }

    /**
     * Обновляет аккаунт менеджера
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void updateManager(Integer accountId, Login login, List<Role> roleList) {
        if (!updateAccount(accountId, login, Account.Type.MANAGER)) {
            return;
        }
        setManagerRoles(accountId, roleList);
    }

    /**
     * Удаляет менеджера
     * @param accountId
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void deleteManager(Integer accountId) {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null || account.getType() != Account.Type.MANAGER) {
            logger.warn("Попытка удалить несуществующий аккаунт или аккаунт неверного типа");
            return;
        }

        offerModerationHelper.onModeratorRemove(accountId);

        accountDAO.delete(accountId);
    }

    /**
     * Регистрирует администратора
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void createAdmin(Login login) {
        Assert.notNull(login);
        Assert.hasText(login.getEmail());
        Assert.hasText(login.getPassword());

        Account account = new Account();

        account.setEmail(login.getEmail());
        account.setPassword(login.getPassword());
        account.setType(Account.Type.ADMIN);

        createAccount(account);
    }

    /**
     * Обновляет администратора
     * @param accountId
     * @param login
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void updateAdmin(Integer accountId, Login login) {
        if (!updateAccount(accountId, login, Account.Type.ADMIN)) {
            return;
        }
    }

    /**
     * Удаляет администратора
     * @param accountId
     */
    @Secured("ROLE_ADMIN")
    @Transactional
    public void deleteAdmin(Integer accountId) {
        Account account = accountDAO.getAccountById(accountId);
        if (account == null || account.getType() != Account.Type.ADMIN) {
            logger.warn("Попытка удалить несуществующий аккаунт или аккаунт неверного типа");
            return;
        }
        if (securityService.getAuthenticatedAccountDetails().getAccountId().equals(accountId)) {
            logger.warn("Попытка удалить свой аккаунт");
            return;
        }
        accountDAO.delete(accountId);
    }

    @Secured("ROLE_ADMIN")
    @Transactional
    public void updateCompany(Integer accountId, Login login, Company company) {
        Assert.notNull(company);

        if (!updateAccount(accountId, login, Account.Type.COMPANY)) {
            return;
        }
        companyDAO.update(company);
    }

    /**
     * Подтверждает адрес электронной почты и аутентифицирует аккаунт
     * @param email
     * @param token
     * @return
     */
    @Transactional
    public boolean confirmEmail(String email, String token) {
        Assert.hasText(token);

        Account acc = getAccountByEmail(email);
        if (acc == null) {
            return false;
        }

        // email уже подтвержден
        if (acc.getEmailAuthenticatedDate() != null) {
            return false;
        }

        Date now = timeService.now();
        // срок действия токена истек
        if (now.getTime() > acc.getEmailTokenExpiresDate().getTime()) {
            return false;
        }

        // неверный токен
        if (!token.equals(acc.getEmailToken())) {
            return false;
        }

        // все OK, можно подтверждать и аутентифицировать
        acc.setEmailAuthenticatedDate(now);
        acc.setLastLoginDate(now);
        accountDAO.update(acc);

        securityService.authenticate(acc);

        return true;
    }

    @Transactional
    public boolean remindPassword(String email) {

        Account acc = getAccountByEmail(email);
        if (acc == null) {
            return false;
        }

        // email не подтвержден
        if (acc.getEmailAuthenticatedDate() == null) {
            return false;
        }

        Date now = timeService.now();
        Date lastTime = acc.getNewPasswordDate();
        if (lastTime != null) {
            if (now.getTime() < DateUtils.addMinutes(lastTime, PASSWORD_REMIND_INTERVAL_MINUTES).getTime()) {
                // слишком часто запрашивает, надо подождать
                return false;
            }
        }

        // все ОК
        String plainPassword = RandomUtils.generateRandomString(PASSWORD_LENGTH,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER,
                RandomUtils.ASCII_UPPER);

        String encodedPassword = encodePassword(acc, plainPassword);

        acc.setNewPassword(encodedPassword);
        acc.setNewPasswordDate(now);

        accountDAO.update(acc);

        sendRemindPasswordEmail(acc.getEmail(), plainPassword);

        return true;
    }

    /**
     * Проверяет валидность пароля аккаунта
     * @param email адрес почты аккаунта
     * @param password пароль
     * @return <code>true</code> если пароль аккаунта верен
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean isPasswordValid(String email, String password) {
        Assert.notNull(password);

        Account account = getAccountByEmail(email);
        if (account == null) {
            return false;
        }

        Object salt = account.getId();
        return passwordEncoder.isPasswordValid(account.getPassword(), password, salt);
    }

    /**
     * Обновляет пароль аккаунта
     * @param email адрес почты аккаунта
     * @param password новый пароль
     * @return <code>true</code> если пароль аккаунта успешно обновлен
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    @Transactional
    public boolean updatePassword(String email, String password) {
        Assert.notNull(password);

        Account account = getAccountByEmail(email);
        if (account == null) {
            return false;
        }

        String encodedPassword = encodePassword(account, password);
        account.setPassword(encodedPassword);
        accountDAO.update(account);

        return true;
    }

    /**
     * Возвращает список менеджеров
     * @param sorting порядок сортировки
     * @return
     */
    @Secured("ROLE_ADMIN")
    @Transactional(readOnly = true)
    public List<Account> getManagerList(AccountSorting sorting) {
        return accountDAO.getAccountListByType(Account.Type.MANAGER, sorting);
    }

    /**
     * Возвращает список администраторов
     * @param sorting порядок сортировки
     * @return
     */
    @Secured("ROLE_ADMIN")
    @Transactional(readOnly = true)
    public List<Account> getAdminList(AccountSorting sorting) {
        return accountDAO.getAccountListByType(Account.Type.ADMIN, sorting);
    }

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER_COMPANY_LOGIN"})
    @Transactional(readOnly = true)
    public Map<Integer, Account> getAccountMap(Set<Integer> accountIdSet) {
        return accountDAO.getAccountMap(accountIdSet);
    }

    @Secured("ROLE_ADMIN")
    @Transactional(readOnly = true)
    public int getRegisteredCompanyCount(Date startDate, Date endDate) {
        return accountDAO.getRegisteredAccountCount(Account.Type.COMPANY, startDate, endDate);
    }

    @Secured("ROLE_MANAGER_COMPANY_LOGIN")
    @Transactional(readOnly = true)
    public void authenticateCompanyAccount(String email) {
        Account account = getAccountByEmail(email);

        Assert.notNull(account);
        Assert.isTrue(Account.Type.COMPANY == account.getType());
        Assert.notNull(account.getEmailAuthenticatedDate());

        securityService.authenticate(account);
    }

    private void sendRemindPasswordEmail(String email, String plainPassword) {
        EmailMessage.Builder builder = new EmailMessage.Builder();

        builder.setFrom(mailFromAddress);
        builder.setTo(email);
        builder.setSubject(messageSource.getMessage("ui.registration.remind.email.subject", null, null));
        builder.setTemplateName("reminder");

        builder.addModelAttribute("email", email);
        builder.addModelAttribute("password", plainPassword);

        logger.debug("Sending new password to: {}", email);

        emailService.sendEmail(builder.build());
    }

    private void sendCompanyRegistrationEmail(String email, String plainPassword, String token) {
        EmailMessage.Builder builder = new EmailMessage.Builder();

        builder.setFrom(mailFromAddress);
        builder.setTo(email);
        builder.setSubject(messageSource.getMessage("ui.registration.confirm.email.subject", null, null));
        builder.setTemplateName("company/registration");

        builder.addModelAttribute("email", email);
        builder.addModelAttribute("password", plainPassword);
        builder.addModelAttribute("token", token);

        logger.debug("Sending company registration email to: {}", email);

        emailService.sendEmail(builder.build());
    }

    private void createAccount(Account account) {
        prepareAccount(account);

        accountDAO.insert(account);

        String encodedPassword = encodePassword(account, account.getPassword());
        account.setPassword(encodedPassword);

        accountDAO.update(account);
    }

    /**
     * Обновляет идентификационные данные аккаунта с заданным <code>id</code>.
     * Пароль аккаунта обновляется если свойство <code>password</code> объекта <code>login</code> не равно <code>null</code>
     * @param accountId
     * @param login
     * @param type
     * @return
     */
    private boolean updateAccount(Integer accountId, Login login, Account.Type type) {
        Assert.notNull(accountId);
        Assert.notNull(login);
        Assert.hasText(login.getEmail());

        Account account = accountDAO.getAccountById(accountId);
        if (account == null || account.getType() != type) {
            logger.warn("Попытка изменить несуществующий аккаунт или аккаунт неверного типа");
            return false;
        }

        account.setEmail(normalizeEmail(login.getEmail()));

        if (login.getPassword() != null) {
            String encodedPassword = encodePassword(account, login.getPassword());
            account.setPassword(encodedPassword);
        }
        accountDAO.update(account);

        return true;
    }

    private void checkCompany(Company company) {
        Assert.notNull(company.getAccount());
        Assert.hasText(company.getName());
    }

    private void prepareCompany(Company company) {
    }

    private void checkAccount(Account account) {
        Assert.hasText(account.getEmail());
        Assert.hasText(account.getPassword());
    }

    private void prepareAccount(Account account) {
        account.setEmail(normalizeEmail(account.getEmail()));

        account.setEmailAuthenticatedDate(null);

        String token = RandomUtils.generateRandomString(EMAIL_TOKEN_LENGTH,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER);

        account.setEmailToken(token);

        Date now = timeService.now();
        Date expiresDate = DateUtils.addDays(now, EMAIL_CONFIRM_INTERVAL_DAYS);
        account.setEmailTokenExpiresDate(expiresDate);

        account.setLastLoginDate(null);
        account.setNewPassword(null);
        account.setNewPasswordDate(null);
        account.setRegistrationDate(now);
    }

    private Account getAccountByEmail(String email) {
        Assert.hasText(email);
        return accountDAO.getAccountByEmail(normalizeEmail(email));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String encodePassword(Account account, String password) {
        Object salt = account.getId();
        return passwordEncoder.encodePassword(password, salt);
    }

    private void setManagerRoles(Integer accountId, List<Role> roleList) {
        Assert.notNull(roleList);

        Set<Role> insertSet = new HashSet<Role>(roleList);
        Assert.isTrue(!insertSet.isEmpty());

        Set<Role> deleteSet = new HashSet<Role>();

        for (Role r : accountDAO.getRoleList(accountId)) {
            if (insertSet.contains(r)) {
                // уже есть такая роль - не добавлять
                insertSet.remove(r);
            } else {
                // нет такой роли - удалить
                deleteSet.add(r);
            }
        }

        for (Role r : deleteSet) {
            accountDAO.deleteRole(accountId, r);
        }
        for (Role r : insertSet) {
            Assert.notNull(r);
            Assert.isTrue(r.isManagerRole());
            accountDAO.insertRole(accountId, r);
        }


        if (insertSet.contains(Role.ROLE_MANAGER_MODERATOR_OFFER)) {
            offerModerationHelper.onModeratorAdd(accountId);
        }
        if (deleteSet.contains(Role.ROLE_MANAGER_MODERATOR_OFFER)) {
            offerModerationHelper.onModeratorRemove(accountId);
        }
    }
}
