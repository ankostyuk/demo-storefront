package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CountryDAO;
import ru.nullpointer.storefront.domain.Country;

/**
 * @author ankostyuk
 */
public class IBatisCountryDAO extends SqlMapClientDaoSupport implements CountryDAO {

    @Override
    public Country getCountryByAlpha2(String alpha2) {
        Assert.notNull(alpha2);
        return (Country) getSqlMapClientTemplate().queryForObject("Country.selectByAlpha2", alpha2);
    }

    @Override
    public List<Country> getCountryList() {
        return (List<Country>) getSqlMapClientTemplate().queryForList("Country.selectList");
    }

    @Override
    public Map<String, Country> getCountryMap(Set<String> countryAlpha2Set) {
        Assert.notNull(countryAlpha2Set);
        if (countryAlpha2Set.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> param = new ArrayList<String>(countryAlpha2Set);
        return (Map<String, Country>) getSqlMapClientTemplate().queryForMap("Country.selectMapByAlpha2Set", param, "alpha2");
    }

    @Override
    public List<Country> getCountryListByText(String text, int limit) {
        Assert.notNull(text);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("text", text);
        parameterMap.put("limit", limit);
        return (List<Country>) getSqlMapClientTemplate().queryForList("Country.selectByText", parameterMap);
    }
}
