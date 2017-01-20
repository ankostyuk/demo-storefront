package ru.nullpointer.storefront.dao;

import java.util.Map;
import ru.nullpointer.storefront.domain.ExtraCurrency;

/**
 *
 * @author Alexander Yastrebov
 */
public interface ExtraCurrencyDAO {

    /**
     * Возвращает карту дополнительных валют поставщика.
     * @param companyId идентификационный номер поставщика
     * @return Карту компаний. Ключем является <code>currency</code> - код дополнительной валюты. 
     *         Карта может быть пустой если поставщика c таким <code>companyId</code> не существует или поставщик использует курсы валют по умолчанию
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    Map<String, ExtraCurrency> getExtraCurrencyMapByCompanyId(Integer companyId);

    /**
     * Сохраняет настройку дополнительной валюты. Устанавливает полученный <code>id</code>
     * @param extraCurrency
     * @throws IllegalArgumentException если <code>extraCurrency</code> равен <code>null</code>
     */
    void insert(ExtraCurrency extraCurrency);

    /**
     * Обновляет настройку дополнительной валюты. Не меняет <code>id</code>, <code>companyId</code>
     * @param extraCurrency
     * @throws IllegalArgumentException если <code>extraCurrency</code> или <code>id</code> равен <code>null</code> 
     */
    void updateInfo(ExtraCurrency extraCurrency);

    /**
     * Удаляет настройку дополнительной валюты.
     * @param extraCurrency
     * @throws IllegalArgumentException если <code>extraCurrency</code> или <code>id</code> равен <code>null</code> 
     */
    void delete(ExtraCurrency extraCurrency);
}
