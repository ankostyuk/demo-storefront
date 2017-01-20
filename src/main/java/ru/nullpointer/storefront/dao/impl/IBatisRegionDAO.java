package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;

import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisRegionDAO extends SqlMapClientDaoSupport implements RegionDAO {

    private List<Region> queryForList(String select, Object param) {
        return (List<Region>) getSqlMapClientTemplate().queryForList(select, param);
    }

    private Region queryForObject(String select, Object param) {
        return (Region) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private Integer queryForSize(String select, Object param) {
        return (Integer) getSqlMapClientTemplate().queryForObject(select, param);
    }

    @Override
    public Region getRegionById(Integer id) {
        Assert.notNull(id);
        return queryForObject("Region.selectById", id);
    }

    @Override
    public List<Region> getRegionsByNameText(String text) {
        Assert.hasText(text);
        // TODO разобраться с алфавитной сортировкой в БД
        return queryForList("Region.selectByNameText", text);
    }

    @Override
    public List<Region> getRegionsPaginatedByNameText(String text, PageConfig pageConfig) {
        Assert.hasText(text);
        Assert.notNull(pageConfig);

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("text", text);
        parameterMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        parameterMap.put("limit", pageConfig.getPageSize());
        // TODO разобраться с алфавитной сортировкой в БД
        return queryForList("Region.selectPaginatedByNameText", parameterMap);
    }

    @Override
    public int getRegionsByNameTextCount(String text) {
        Assert.hasText(text);
        return queryForSize("Region.countByNameText", text);
    }

    @Override
    public List<Region> getRegionPath(Region region) {
        Assert.notNull(region);
        Assert.notNull(region.getLeft());
        Assert.notNull(region.getRight());
        Assert.isTrue(region.getLeft() < region.getRight());
        return queryForList("Region.selectPath", region);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, Region> getRegionMap(Set<Integer> regionIdSet) {
        Assert.notNull(regionIdSet);
        if (regionIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        return getSqlMapClientTemplate().queryForMap("Region.selectMapByIdSet", new ArrayList<Integer>(regionIdSet), "id");
    }

    @Override
    public List<Region> getCompanyDeliveryRegionList(Integer companyId) {
        Assert.notNull(companyId);
        return queryForList("Region.selectCompanyDelivery", companyId);
    }

    @Override
    public void insertCompanyDeliveryRegion(Integer companyId, Integer regionId) {
        Assert.notNull(companyId);
        Assert.notNull(regionId);

        Map<String, Integer> paramMap = new HashMap<String, Integer>();
        paramMap.put("companyId", companyId);
        paramMap.put("regionId", regionId);

        getSqlMapClientTemplate().insert("Region.insertCompanyDelivery", paramMap);
    }

    @Override
    public void deleteCompanyDeliveryRegion(Integer companyId, Integer regionId) {
        Assert.notNull(companyId);
        Assert.notNull(regionId);

        Map<String, Integer> paramMap = new HashMap<String, Integer>();
        paramMap.put("companyId", companyId);
        paramMap.put("regionId", regionId);

        getSqlMapClientTemplate().delete("Region.deleteCompanyDelivery", paramMap);
    }
}
