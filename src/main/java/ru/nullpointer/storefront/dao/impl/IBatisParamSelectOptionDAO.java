package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ParamSelectOptionDAO;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;

/**
 * 
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
public class IBatisParamSelectOptionDAO extends SqlMapClientDaoSupport implements ParamSelectOptionDAO {

    private OrdinalHelper ordinalHelper = new OrdinalHelper("param_select_option", "pso_id", "pso_param_id", "pso_ordinal");
    //

    private ParamSelectOption queryForObject(String select, Object param) {
        return (ParamSelectOption) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private List<ParamSelectOption> queryForList(String select, Object param) {
        return (List<ParamSelectOption>) getSqlMapClientTemplate().queryForList(select, param);
    }

    @Override
    public ParamSelectOption getParamSelectOptionById(Integer id) {
        Assert.notNull(id);
        return queryForObject("ParamSelectOption.selectById", id);
    }

    @Override
    public List<ParamSelectOption> getParamSelectOptionsByParamId(Integer paramId) {
        Assert.notNull(paramId);
        return queryForList("ParamSelectOption.selectByParamId", paramId);
    }

    @Override
    public Map<Integer, List<ParamSelectOption>> getParamSelectOptionMap(Set<Integer> paramIdSet) {
        Assert.notNull(paramIdSet);
        if (paramIdSet.isEmpty()) {
            return Collections.emptyMap();
        }

        /**
         * Важно чтобы возвращаемый список был отсортирован по id параметра, а затем по ordinal
         */
        List<ParamSelectOption> optionList = queryForList("ParamSelectOption.selectByParamIdList", new ArrayList<Integer>(paramIdSet));

        Map<Integer, List<ParamSelectOption>> result = new HashMap<Integer, List<ParamSelectOption>>(optionList.size());
        for (ParamSelectOption option : optionList) {
            List<ParamSelectOption> list = result.get(option.getParamId());
            if (list == null) {
                list = new ArrayList<ParamSelectOption>();
                list.add(option);
                result.put(option.getParamId(), list);
            } else {
                list.add(option);
            }
        }
        return result;
    }

    @Override
    public void insert(ParamSelectOption paramSelectOption) {
        Assert.notNull(paramSelectOption);
        Assert.notNull(paramSelectOption.getParamId());
        Assert.hasText(paramSelectOption.getName());

        getSqlMapClientTemplate().insert("ParamSelectOption.insert", paramSelectOption);
        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), paramSelectOption.getId(), -1);
        paramSelectOption.setOrdinal(ordinal);
    }

    @Override
    public void updateInfo(ParamSelectOption paramSelectOption) {
        Assert.notNull(paramSelectOption);
        Assert.notNull(paramSelectOption.getId());
        Assert.hasText(paramSelectOption.getName());
        getSqlMapClientTemplate().update("ParamSelectOption.updateInfo", paramSelectOption);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        SqlMapClientTemplate template = getSqlMapClientTemplate();

        ordinalHelper.deleteOrdinal(template, id);
        template.delete("ParamSelectOption.delete", id, 1);
    }

    @Override
    public void updateOrder(ParamSelectOption paramSelectOption) {
        Assert.notNull(paramSelectOption);
        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), paramSelectOption.getId(), paramSelectOption.getOrdinal());
        paramSelectOption.setOrdinal(ordinal);
    }
}
