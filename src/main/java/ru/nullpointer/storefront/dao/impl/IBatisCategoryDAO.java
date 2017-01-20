package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.domain.Category;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisCategoryDAO extends SqlMapClientDaoSupport implements CategoryDAO {

    private Category queryForObject(String select, Object param) {
        return (Category) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private Map<Integer, Category> queryForMap(String select, Object param, String property) {
        return (Map<Integer, Category>) getSqlMapClientTemplate().queryForMap(select, param, property);
    }

    private List<Category> queryForList(String select, Object param) {
        return (List<Category>) getSqlMapClientTemplate().queryForList(select, param);
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        Assert.notNull(categoryId);
        return queryForObject("Category.selectById", categoryId);
    }

    @Override
    public Map<Integer, Category> getCategoryMap(Set<Integer> categoryIdSet) {
        Assert.notNull(categoryIdSet);
        if (categoryIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем в список
        List<Integer> param = new ArrayList<Integer>(categoryIdSet);
        return queryForMap("Category.selectMapByIdSet", param, "id");
    }
    
    @Override
    public List<Category> getAllCategories() {
        return queryForList("Category.selectAll", null);
    }
    
    @Override
    public void addCategory(Category category) {
        Assert.notNull(category);
        Assert.notNull(category.getId());
        Assert.notNull(category.getUnitId());
        getSqlMapClientTemplate().insert("Category.insert", category);    
    }
    
    @Override
    public void updateCategoryInfo(Category category) {
        Assert.notNull(category);
        Assert.notNull(category.getId());
        Assert.notNull(category.getUnitId());
        getSqlMapClientTemplate().update("Category.updateInfo", category);
    }
    
    @Override
    public void deleteCategory(Integer categoryId) {
        Assert.notNull(categoryId);
        getSqlMapClientTemplate().delete("Category.delete", categoryId, 1);
    }
    
    @Override
    public void updateCategoryParameterSetDescriptor(Category category) {
        Assert.notNull(category);
        Assert.notNull(category.getId());
        getSqlMapClientTemplate().update("Category.updateParameterSetDescriptor", category);
    }
    
    @Override
    public Category getCategoryByParamSetDescriptorId(Integer psdId) {
        Assert.notNull(psdId);
        return queryForObject("Category.selectByParamSetDescriptorId", psdId);
    }
}
