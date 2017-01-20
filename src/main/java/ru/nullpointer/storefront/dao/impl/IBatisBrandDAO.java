package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.domain.Brand;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisBrandDAO extends SqlMapClientDaoSupport implements BrandDAO {

    @Override
    public int getBrandCount() {
        return (Integer) getSqlMapClientTemplate().queryForObject("Brand.count", null);
    }

    @Override
    public List<Brand> getBrandList() {
        return (List<Brand>) getSqlMapClientTemplate().queryForList("Brand.selectList");
    }

    @Override
    public List<String> getBrandNamePrefixList() {
        return (List<String>) getSqlMapClientTemplate().queryForList("Brand.selectPrefixList");
    }

    @Override
    public Brand getBrandById(Integer id) {
        Assert.notNull(id);
        return (Brand) getSqlMapClientTemplate().queryForObject("Brand.selectById", id);
    }

    @Override
    public Map<Integer, Brand> getBrandMap(Set<Integer> brandIdSet) {
        Assert.notNull(brandIdSet);
        if (brandIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Integer> param = new ArrayList<Integer>(brandIdSet);
        return getSqlMapClientTemplate().queryForMap("Brand.selectMapByIdSet", param, "id");
    }

    @Override
    public List<Brand> getBrandListByIdSet(Set<Integer> brandIdSet) {
        Assert.notNull(brandIdSet);
        if (brandIdSet.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> param = new ArrayList<Integer>(brandIdSet);
        return getSqlMapClientTemplate().queryForList("Brand.selectByIdSet", param);
    }

    @Override
    public List<Brand> findBrandListByText(String text, int limit) {
        Assert.notNull(text);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("text", text);
        parameterMap.put("limit", limit);
        return (List<Brand>) getSqlMapClientTemplate().queryForList("Brand.findByText", parameterMap);
    }

    @Override
    public List<Brand> getCategoryBrandList(Integer categoryId) {
        Assert.notNull(categoryId);
        return (List<Brand>) getSqlMapClientTemplate().queryForList("Brand.selectByCategoryId", categoryId);
    }

    @Override
    public List<Brand> getIntersectionList(Set<Integer> brandIdSet, Set<Integer> categoryIdSet) {
        Assert.notNull(brandIdSet);
        Assert.notNull(categoryIdSet);

        if (brandIdSet.isEmpty() || categoryIdSet.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> brandIdList = new ArrayList<Integer>(brandIdSet);
        List<Integer> categoryIdList = new ArrayList<Integer>(categoryIdSet);

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("brandIdList", brandIdList);
        parameterMap.put("categoryIdList", categoryIdList);

        return (List<Brand>) getSqlMapClientTemplate().queryForList("Brand.selectIntersectionBrandCategory", parameterMap);
    }

    @Override
    public void insert(Brand brand) {
        Assert.notNull(brand);
        getSqlMapClientTemplate().insert("Brand.insert", brand);
    }

    @Override
    public void updateInfo(Brand brand) {
        Assert.notNull(brand);
        Assert.notNull(brand.getId());
        getSqlMapClientTemplate().update("Brand.updateInfo", brand, 1);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Brand.delete", id, 1);
    }
}
