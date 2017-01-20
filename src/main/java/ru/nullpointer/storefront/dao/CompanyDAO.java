package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 *
 * @author Alexander Yastrebov
 */
public interface CompanyDAO {

    /**
     * Возвращает поставщика по <code>id</code>
     * @param id <code>id</code> компании
     * @return компания или <code>null</code> если компании с таким <code>id</code> не существует
     */
    Company getCompanyById(int id);

    /**
     * Возвращает карту поставщиков.
     * @param companyIdSet набор <code>id</code> компаний
     * @return Карту компаний. Ключем является <code>id</code> компании. Карта может быть пустой если список <code>companyIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>companyIdSet</code> равен <code>null</code>
     */
    Map<Integer, Company> getCompanyMap(Set<Integer> companyIdSet);

    /**
     * Возвращает список поставщиков отсортированный по убыванию даты подтверждения емейла
     * @param text текст в наименовании поставщика или <code>null</code> для всех поставщиков
     * @param pageConfig
     * @return
     */
    List<Company> getCompanyList(String text, PageConfig pageConfig);

    /**
     * Возвращает количество поставщиков
     * @param text текст в наименовании поставщика или <code>null</code> для всех поставщиков
     * @return
     */
    int getCompanyCount(String text);

    /**
     * Сохраняет поставщика. Устанавливает поставщику полученный <code>id</code>
     * @param company
     * @throws IllegalArgumentException если <code>company</code> равен <code>null</code> 
     *         или поле <code>id</code> равно <code>null</code>
     *         или поле <code>regionId</code> равно <code>null</code>
     */
    void insert(Company company);

    /**
     * Обновляет поставщика.
     * @param company
     * @throws IllegalArgumentException если <code>company</code> равен <code>null</code> 
     * или <code>id</code> компании равен <code>null<code>
     */
    void update(Company company);

    /**
     * Обновляет информацию о поставщике. Не меняет <code>id</code>, <code>name</code>
     * @param company
     * @throws IllegalArgumentException если <code>company</code> равен <code>null</code>
     * или <code>id</code> компании равен <code>null<code>
     */
    void updateInfo(Company company);
}
