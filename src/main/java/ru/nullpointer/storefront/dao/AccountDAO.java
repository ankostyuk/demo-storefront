package ru.nullpointer.storefront.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.AccountSorting;

/**
 *
 * @author Alexander Yastrebov
 */
public interface AccountDAO {

    /**
     * Проверяет доступность адреса электронной почты для регистрации аккаунта
     * @param email
     * @return <code>false</code> если существует аккаунт зарегистрированный на этот адрес
     * @throws IllegalArgumentException если <code>email</code> пустая строка или <code>null</code>
     */
    boolean isEmailAvailable(String email);

    /**
     * Возвращает аккаунт по email
     * @param email email аккаунта
     * @return аккаунт или <code>null</code> если аккаунта с таким email не существует
     * @throws IllegalArgumentException если <code>email</code> пустая строка или <code>null</code>
     */
    Account getAccountByEmail(String email);

    /**
     * Возвращает аккаунт по идентификационному номеру
     * @param id
     * @return аккаунт или <code>null</code> если аккаунта с таким номером не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Account getAccountById(Integer id);

    /**
     * Возвращает отсортированный список аккаунтов заданного типа
     * @param type
     * @param sorting
     * @return
     */
    List<Account> getAccountListByType(Account.Type type, AccountSorting sorting);

    /**
     * Возвращает карту аккаунтов. Ключем карты является ИД аккаунта
     * @param accountIdSet набор ИД аккаунтов
     * @return
     * @throws IllegalArgumentException если <code>accountIdSet</code> равен <code>null</code> или пуст
     */
    Map<Integer, Account> getAccountMap(Set<Integer> accountIdSet);

    /**
     * Сохраняет аккаунт. Устанавливает аккаунту полученный <code>id</code>
     * @param account
     * @throws IllegalArgumentException если <code>account</code> равен <code>null</code>
     */
    void insert(Account account);

    /**
     * Обновляет аккаунт.
     * @param account
     * @throws IllegalArgumentException если <code>account</code> равен <code>null</code> 
     * или <code>id</code> аккаунта равен <code>null<code>
     */
    void update(Account account);

    /**
     * Удаляет аккаунт
     * @param id
     */
    void delete(Integer id);

    /**
     * Возвращает список ролей к которым принадлежит аккаунт
     * @param accountId
     * @return список ролей. список может быть пустым если аккаунт не принадлежит
     * ни к одной роли, или аккаунта с таким <code>accountId</code> не существует
     */
    List<Role> getRoleList(Integer accountId);

    /**
     * Устанавливает принадлежность аккаунта к роли
     * @param accountId идентификационный номер аккаунта
     * @param role роль
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    void insertRole(Integer accountId, Role role);

    /**
     * Снимает принадлежность аккаунта к роли
     * @param accountId идентификационный номер аккаунта
     * @param role роль
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    void deleteRole(Integer accountId, Role role);

    /**
     * Возвращает список идентификаторов аккаунтов, принадлежащих роли <code>role</code>
     * @param role
     * @return
     * @throws IllegalArgumentException если <code>role<code> равен <code>null</code>
     */
    public List<Integer> getAccountIdListFromRole(Role role);

    /**
     * Возвращает количество зарегистрированных аккаунтов (подтвердивших свой емайл)
     * за период времени.
     * @param type тип аккаунта
     * @param startDate начало периода или <code>null</code>
     * @param endDate конец периода или <code>null</code>
     * @return
     */
    int getRegisteredAccountCount(Account.Type type, Date startDate, Date endDate);
}
