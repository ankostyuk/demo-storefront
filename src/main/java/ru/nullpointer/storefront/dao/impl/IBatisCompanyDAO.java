package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisCompanyDAO extends SqlMapClientDaoSupport implements CompanyDAO {

    private Company queryForObject(String select, Object param) {
        return (Company) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private Map<Integer, Company> queryForMap(String select, Object param, String property) {
        return (Map<Integer, Company>) getSqlMapClientTemplate().queryForMap(select, param, property);
    }

    @Override
    public Company getCompanyById(int accountId) {
        return queryForObject("Company.selectById", accountId);
    }

    @Override
    public List<Company> getCompanyList(String text, PageConfig pageConfig) {
        Assert.notNull(pageConfig);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("text", StringUtils.isBlank(text) ? null : text);
        paramMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        paramMap.put("limit", pageConfig.getPageSize());

        return (List<Company>) getSqlMapClientTemplate().queryForList("Company.selectByText", paramMap);
    }

    @Override
    public int getCompanyCount(String text) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("text", StringUtils.isBlank(text) ? null : text);

        return (Integer) getSqlMapClientTemplate().queryForObject("Company.countByText", paramMap);
    }

    @Override
    public void insert(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getId());
        Assert.notNull(company.getRegionId());
        getSqlMapClientTemplate().insert("Company.insert", company);
    }

    @Override
    public void update(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getId());
        getSqlMapClientTemplate().update("Company.update", company, 1);
    }

    @Override
    public void updateInfo(Company company) {
        Assert.notNull(company);
        Assert.notNull(company.getId());
        getSqlMapClientTemplate().update("Company.updateInfo", company, 1);
    }

    @Override
    public Map<Integer, Company> getCompanyMap(Set<Integer> companyIdSet) {
        Assert.notNull(companyIdSet);
        if (companyIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем в список
        List<Integer> param = new ArrayList<Integer>(companyIdSet);
        return queryForMap("Company.selectMapByIdSet", param, "id");
    }
}
