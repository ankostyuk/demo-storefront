package ru.nullpointer.storefront.dao.impl;

import java.util.List;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ParamGroupDAO;
import ru.nullpointer.storefront.domain.param.ParamGroup;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
public class IBatisParamGroupDAO extends SqlMapClientDaoSupport implements ParamGroupDAO {

    private OrdinalHelper ordinalHelper = new OrdinalHelper("param_group", "pg_id", "pg_psd_id", "pg_ordinal");
    //

    private ParamGroup queryForObject(String select, Object param) {
        return (ParamGroup) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private List<ParamGroup> queryForList(String select, Object param) {
        return (List<ParamGroup>) getSqlMapClientTemplate().queryForList(select, param);
    }

    @Override
    public ParamGroup getParamGroupById(Integer id) {
        Assert.notNull(id);
        return queryForObject("ParamGroup.selectById", id);
    }

    @Override
    public List<ParamGroup> getAllParamGroups() {
        return queryForList("ParamGroup.selectAll", null);
    }

    @Override
    public List<ParamGroup> getParamGroupsByParamSetDescriptorId(Integer psdId) {
        Assert.notNull(psdId);
        return queryForList("ParamGroup.selectByParamSetDescriptorId", psdId);
    }

    @Override
    public void insert(ParamGroup paramGroup) {
        Assert.notNull(paramGroup);
        Assert.notNull(paramGroup.getParameterSetDescriptorId());
        Assert.hasText(paramGroup.getName());
        
        getSqlMapClientTemplate().insert("ParamGroup.insert", paramGroup);
        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), paramGroup.getId(), -1);
        paramGroup.setOrdinal(ordinal);
    }

    @Override
    public void updateInfo(ParamGroup paramGroup) {
        Assert.notNull(paramGroup);
        Assert.notNull(paramGroup.getId());
        Assert.hasText(paramGroup.getName());
        getSqlMapClientTemplate().update("ParamGroup.updateInfo", paramGroup);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        SqlMapClientTemplate template = getSqlMapClientTemplate();

        ordinalHelper.deleteOrdinal(template, id);
        template.delete("ParamGroup.delete", id, 1);
    }

    @Override
    public void updateOrder(ParamGroup paramGroup) {
        Assert.notNull(paramGroup);
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), paramGroup.getId(), paramGroup.getOrdinal());
        paramGroup.setOrdinal(ordinal);
    }
}
