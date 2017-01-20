package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;

/**
 *
 * @author ankostyuk
 */
public class IBatisParamSetDAO extends SqlMapClientDaoSupport implements ParamSetDAO {

    private ParamSetDescriptor queryForObject(String select, Object param) {
        return (ParamSetDescriptor) getSqlMapClientTemplate().queryForObject(select, param);
    }

    @Override
    public ParamSetDescriptor getParamSetDescriptorById(Integer id) {
        Assert.notNull(id);
        return queryForObject("ParamSet.selectDescriptorById", id);
    }

    @Override
    public void addParamSetDescriptor(ParamSetDescriptor psd) {
        Assert.notNull(psd);
        Assert.hasText(psd.getName());
        Assert.hasText(psd.getTableName());
        getSqlMapClientTemplate().insert("ParamSet.insertDescriptor", psd);
    }

    @Override
    public void deleteParamSetDescriptor(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("ParamSet.deleteDescriptor", id, 1);
    }

    @Override
    public Map<Integer, Object> getParamSetById(Integer paramDescriptorId, Integer id) {
        Assert.notNull(paramDescriptorId);
        Assert.notNull(id);

        Set<Integer> idSet = new HashSet<Integer>();
        idSet.add(id);

        Map<Integer, Object> result = getParamSetMap(paramDescriptorId, idSet).get(id);
        Assert.notNull(result);
        return result;
    }

    @Override
    public Map<Integer, Map<Integer, Object>> getParamSetMap(Integer paramDescriptorId, Set<Integer> paramSetIdSet) {
        Assert.notNull(paramDescriptorId);
        Assert.notNull(paramSetIdSet);
        Assert.isTrue(!paramSetIdSet.isEmpty());

        ParamSetDescriptor psd = getParamSetDescriptorById(paramDescriptorId);
        Assert.notNull(psd);

        List<Param> paramList = (List<Param>) getSqlMapClientTemplate().queryForList("Param.selectByDescriptorId", psd.getId());
        if (paramList.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> columnNameList = new ArrayList<String>();
        for (Param p : paramList) {
            columnNameList.add(p.getColumnName());
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tableName", psd.getTableName());
        paramMap.put("columnNameList", columnNameList);
        paramMap.put("idList", new ArrayList<Integer>(paramSetIdSet));

        List<Map<String, Object>> valueList = (List<Map<String, Object>>) getSqlMapClientTemplate().queryForList("ParamSet.select", paramMap);
        Assert.notNull(valueList);

        Map<Integer, Map<Integer, Object>> resultMap = new HashMap<Integer, Map<Integer, Object>>(valueList.size());
        for (Map<String, Object> valueMap : valueList) {
            Integer id = (Integer) valueMap.get("id");
            Map<Integer, Object> result = new HashMap<Integer, Object>();
            for (Param p : paramList) {
                Object value = valueMap.get(p.getColumnName());
                result.put(p.getId(), value);
            }
            resultMap.put(id, result);
        }
        return resultMap;
    }

    @Override
    public Integer insert(Integer paramDescriptorId, Map<Integer, Object> paramValueMap) {
        Assert.notNull(paramDescriptorId);
        Assert.notNull(paramValueMap);

        Map<String, Object> paramMap = initModificationParamMap(paramDescriptorId, paramValueMap);

        return (Integer) getSqlMapClientTemplate().insert("ParamSet.insert", paramMap);
    }

    @Override
    public void update(Integer paramDescriptorId, Integer id, Map<Integer, Object> paramValueMap) {
        Assert.notNull(paramDescriptorId);
        Assert.notNull(id);
        Assert.notNull(paramValueMap);

        Map<String, Object> paramMap = initModificationParamMap(paramDescriptorId, paramValueMap);
        paramMap.put("id", id);

        getSqlMapClientTemplate().update("ParamSet.update", paramMap, 1);
    }

    @Override
    public void delete(Integer paramDescriptorId, Integer id) {
        Assert.notNull(paramDescriptorId);
        Assert.notNull(id);

        ParamSetDescriptor psd = getParamSetDescriptorById(paramDescriptorId);
        Assert.notNull(psd);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tableName", psd.getTableName());
        paramMap.put("id", id);

        getSqlMapClientTemplate().delete("ParamSet.delete", paramMap, 1);
    }

    private Map<String, Object> initModificationParamMap(Integer paramDescriptorId, Map<Integer, Object> paramValueMap) {
        ParamSetDescriptor psd = getParamSetDescriptorById(paramDescriptorId);
        Assert.notNull(psd);

        List<NameValueHolder> columnList = new ArrayList<NameValueHolder>();

        List<Param> paramList = (List<Param>) getSqlMapClientTemplate().queryForList("Param.selectByDescriptorId", psd.getId());
        // вывести все колонки, независимо от наличия значений, для кеширования PreparedStatement
        for (Param p : paramList) {
            String columnName = p.getColumnName();
            Object value = paramValueMap.get(p.getId());
            columnList.add(new NameValueHolder(columnName, value));
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tableName", psd.getTableName());
        paramMap.put("columnList", columnList);

        return paramMap;
    }
}
