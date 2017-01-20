package ru.nullpointer.storefront.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class CurrencyConfig implements InitializingBean {

    private String defaultCurrency;
    private List<String> currencyList;
    //
    private List<String> extraCurrencyList;

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public List<String> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<String> currencyList) {
        this.currencyList = currencyList;
    }

    /**
     * Возвращает список валют за исключение валюты по-умолчанию
     * @return
     */
    public List<String> getExtraCurrencyList() {
        return extraCurrencyList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(defaultCurrency, "Не указана валюта по-умолчанию");
        Assert.notNull(currencyList, "Не указан список валют");
        Assert.notEmpty(currencyList, "Список валют не должен быть пуст");
        for (String c : currencyList) {
            Assert.hasText(c, "Список валют не должен содержать пустых значений");
        }
        Assert.isTrue(currencyList.contains(defaultCurrency), "Валюта по-умолчанию должна содержаться в списке валют");

        // Список дополнительных валют
        extraCurrencyList = new ArrayList<String>();
        for (String c : currencyList) {
            if (!c.equals(defaultCurrency)) {
                extraCurrencyList.add(c);
            }
        }
    }
}
