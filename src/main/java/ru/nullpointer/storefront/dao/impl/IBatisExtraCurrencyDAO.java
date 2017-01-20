package ru.nullpointer.storefront.dao.impl;

import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ExtraCurrencyDAO;
import ru.nullpointer.storefront.domain.ExtraCurrency;

/**
 *
 * @author Alexander
 */
public class IBatisExtraCurrencyDAO extends SqlMapClientDaoSupport implements ExtraCurrencyDAO {

    private Map<String, ExtraCurrency> queryForMap(String select, Object param, String property) {
        return (Map<String, ExtraCurrency>) getSqlMapClientTemplate().queryForMap(select, param, property);
    }

    @Override
    public Map<String, ExtraCurrency> getExtraCurrencyMapByCompanyId(Integer companyId) {
        Assert.notNull(companyId);
        Map<String, ExtraCurrency> result = queryForMap("ExtraCurrency.selectMapByCompanyId", companyId, "currency");
        for (ExtraCurrency ec : result.values()) {
            if (ec.getPercent() != null) {
                Assert.isNull(ec.getFixedRate());
                ec.setType(ExtraCurrency.Type.PERCENT);
            } else {
                Assert.notNull(ec.getFixedRate());
                ec.setType(ExtraCurrency.Type.FIXED_RATE);
            }
        }
        return result;
    }

    @Override
    public void insert(ExtraCurrency extraCurrency) {
        Assert.notNull(extraCurrency);
        getSqlMapClientTemplate().insert("ExtraCurrency.insert", extraCurrency);
    }

    @Override
    public void updateInfo(ExtraCurrency extraCurrency) {
        Assert.notNull(extraCurrency);
        Assert.notNull(extraCurrency.getId());
        getSqlMapClientTemplate().update("ExtraCurrency.updateInfo", extraCurrency, 1);
    }

    @Override
    public void delete(ExtraCurrency extraCurrency) {
        Assert.notNull(extraCurrency);
        Assert.notNull(extraCurrency.getId());
        getSqlMapClientTemplate().delete("ExtraCurrency.delete", extraCurrency.getId(), 1);
    }
}
