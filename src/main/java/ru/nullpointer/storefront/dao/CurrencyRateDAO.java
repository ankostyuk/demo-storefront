package ru.nullpointer.storefront.dao;

import ru.nullpointer.storefront.domain.CurrencyRate;

/**
 *
 * @author Alexander
 */
public interface CurrencyRateDAO {

    /**
     * Возвращает значение курса валют
     * @param fromCurrency исходная валюта
     * @param toCurrency котируемая валюта
     * @return значение курса валют или <code>null</code> если такой комбинации исходной и котируемой валюты не существует
     * @throws IllegalArgumentException если <code>fromCurrency</code> или <code>toCurrency</code> равен <code>null</code>
     */
    CurrencyRate getCurrencyRate(String fromCurrency, String toCurrency);

    /**
     * Обновляет значение курса валют
     * @param currencyRate
     * @throws IllegalArgumentException если <code>currencyRate</code> или <code>fromCurrency</code> или <code>toCurrency</code> равен <code>null</code>
     */
    void update(CurrencyRate currencyRate);
}
