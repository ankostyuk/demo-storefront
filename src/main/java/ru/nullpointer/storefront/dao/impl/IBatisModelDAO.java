package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisModelDAO extends SqlMapClientDaoSupport implements ModelDAO {

    @Override
    public List<Model> getCategoryModelList(Integer categoryId, PageConfig pageConfig) {
        Assert.notNull(categoryId);
        Assert.notNull(pageConfig);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("categoryId", categoryId);
        paramMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        paramMap.put("limit", pageConfig.getPageSize());

        return (List<Model>) getSqlMapClientTemplate().queryForList("Model.selectByCategoryId", paramMap);
    }

    @Override
    public int getCategoryModelCount(Integer categoryId) {
        Assert.notNull(categoryId);

        return (Integer) getSqlMapClientTemplate().queryForObject("Model.countByCategoryId", categoryId);
    }

    @Override
    public Map<Integer, Integer> getCategoryModelCountMap(Set<Integer> categoryIdSet) {
        Assert.notNull(categoryIdSet);
        return (Map<Integer, Integer>) getSqlMapClientTemplate().queryForMap("Model.countMapByCategoryId",
                new ArrayList<Integer>(categoryIdSet), "id", "count");
    }

    @Override
    public Model getModelById(Integer id) {
        Assert.notNull(id);
        return (Model) getSqlMapClientTemplate().queryForObject("Model.selectById", id);
    }

    @Override
    public Map<Integer, Model> getModelMap(Set<Integer> modelIdSet) {
        Assert.notNull(modelIdSet);
        if (modelIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем в список
        List<Integer> param = new ArrayList<Integer>(modelIdSet);
        return getSqlMapClientTemplate().queryForMap("Model.selectMapByIdSet", param, "id");
    }

    @Override
    public List<Model> findModelListByText(String text, Integer categoryId, Integer brandId, int limit) {
        Assert.notNull(text);
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("text", text);
        paramMap.put("categoryId", categoryId);
        paramMap.put("brandId", brandId);
        paramMap.put("limit", limit);

        return (List<Model>) getSqlMapClientTemplate().queryForList("Model.findByText", paramMap);
    }

    @Override
    public void insert(Model model) {
        Assert.notNull(model);
        getSqlMapClientTemplate().insert("Model.insert", model);
    }

    @Override
    public void updateInfo(Model model) {
        Assert.notNull(model);
        Assert.notNull(model.getId());
        getSqlMapClientTemplate().update("Model.updateInfo", model, 1);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Model.delete", id, 1);
    }
}
