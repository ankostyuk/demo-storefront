package ru.nullpointer.storefront.service.support;

import java.util.Date;
import java.util.List;
import ru.nullpointer.storefront.domain.CurrencyRate;

/**
 *
 * @author Alexander Yastrebov
 */
public interface CurrencyRateRetriever {

    /**
     * Возвращает список котировок валют.
     * Длина списка &lt;= количеству дополнительных валют. Свойство <code>toCurrency</code> каждого элемента равно значению <code>defaultCurrency</code>
     * @param defaultCurrency
     * @param extraCurrencyList
     * @param date
     * @return
     */
    List<CurrencyRate> getRates(String defaultCurrency, List<String> extraCurrencyList, Date date);
}
