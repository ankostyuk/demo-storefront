package ru.nullpointer.storefront.dao.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CurrencyRateDAO;
import ru.nullpointer.storefront.domain.CurrencyRate;

/**
 *
 * @author Alexander
 */
public class IBatisCurrencyRateDAO extends SqlMapClientDaoSupport implements CurrencyRateDAO {

    private CurrencyRate queryForObject(String select, Object param) {
        return (CurrencyRate) getSqlMapClientTemplate().queryForObject(select, param);
    }

    @Override
    public CurrencyRate getCurrencyRate(String from, String to) {
        Assert.notNull(from);
        Assert.notNull(to);
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("fromCurrency", from);
        paramMap.put("toCurrency", to);
        
        return queryForObject("CurrencyRate.selectByCurrencies", paramMap);
    }

    @Override
    public void update(CurrencyRate currencyRate) {
        Assert.notNull(currencyRate);
        Assert.notNull(currencyRate.getFromCurrency());
        Assert.notNull(currencyRate.getToCurrency());
        
        getSqlMapClientTemplate().update("CurrencyRate.update", currencyRate, 1);
    }
}
