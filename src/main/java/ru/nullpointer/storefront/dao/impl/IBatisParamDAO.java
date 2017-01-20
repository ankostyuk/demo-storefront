package ru.nullpointer.storefront.dao.impl;

import java.util.List;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.SelectParam;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
public class IBatisParamDAO extends SqlMapClientDaoSupport implements ParamDAO {

    private Logger logger = LoggerFactory.getLogger(IBatisParamDAO.class);
    //
    private OrdinalHelper ordinalHelper = new OrdinalHelper("param", "par_id", "par_pg_id", "par_ordinal");
    //

    private Param queryForObject(String select, Object param) {
        return (Param) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private List<Param> queryForList(String select, Object param) {
        return (List<Param>) getSqlMapClientTemplate().queryForList(select, param);
    }

    @Override
    public Param getParamById(Integer id) {
        Assert.notNull(id);
        return queryForObject("Param.selectById", id);
    }

    @Override
    public List<Param> getAllParams() {
        return queryForList("Param.selectAll", null);
    }

    @Override
    public List<Param> getParamsByParamGroupId(Integer paramGroupId) {
        Assert.notNull(paramGroupId);
        return queryForList("Param.selectByParamGroupId", paramGroupId);
    }

    @Override
    public List<Param> getParamListByDescriptorId(Integer psdId) {
        Assert.notNull(psdId);
        return queryForList("Param.selectByDescriptorId", psdId);
    }

    @Override
    public void insert(Param param) {
        Assert.notNull(param);

        getSqlMapClientTemplate().insert("Param.insert", param);
        switch (param.getType()) {
            case BOOLEAN:
                Assert.isInstanceOf(BooleanParam.class, param);
                getSqlMapClientTemplate().insert("Param.insertBoolean", param);
                break;

            case NUMBER:
                Assert.isInstanceOf(NumberParam.class, param);
                getSqlMapClientTemplate().insert("Param.insertNumber", param);
                break;

            case SELECT:
                Assert.isInstanceOf(SelectParam.class, param);
                // нечего делать
                break;

            default:
                throw new RuntimeException("Неизвестный тип параметра \"" + param.getType() + "\"");
        }
        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), param.getId(), -1);
        param.setOrdinal(ordinal);
    }

    @Override
    public void updateInfo(Param param) {
        Assert.notNull(param);
        Assert.notNull(param.getId());

        getSqlMapClientTemplate().update("Param.updateInfo", param, 1);

        switch (param.getType()) {
            case BOOLEAN:
                Assert.isInstanceOf(BooleanParam.class, param);
                getSqlMapClientTemplate().update("Param.updateBooleanInfo", param, 1);
                break;

            case NUMBER:
                Assert.isInstanceOf(NumberParam.class, param);
                getSqlMapClientTemplate().update("Param.updateNumberInfo", param);
                break;

            case SELECT:
                Assert.isInstanceOf(SelectParam.class, param);
                // нечего делать
                break;

            default:
                throw new RuntimeException("Неизвестный тип параметра \"" + param.getType() + "\"");
        }
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        SqlMapClientTemplate template = getSqlMapClientTemplate();

        ordinalHelper.deleteOrdinal(template, id);
        template.delete("Param.delete", id, 1);
    }

    @Override
    public void updateOrder(Param param) {
        Assert.notNull(param);
        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(getSqlMapClientTemplate(), param.getId(), param.getOrdinal());
        param.setOrdinal(ordinal);
    }

    @Override
    public void updateParamGroup(Param param) {
        Assert.notNull(param);
        Assert.notNull(param.getId());
        Assert.notNull(param.getParamGroupId());
        Assert.notNull(param.getOrdinal());

        SqlMapClientTemplate template = getSqlMapClientTemplate();

        ordinalHelper.deleteOrdinal(template, param.getId());
        template.update("Param.updateGroup", param);

        // установить порядковый номер
        Integer ordinal = ordinalHelper.updateOrdinal(template, param.getId(), -1);
        param.setOrdinal(ordinal);
    }

    @Override
    public void updateBase(Param param) {
        Assert.notNull(param);
        Assert.notNull(param.getId());
        Assert.notNull(param.getBase());
        getSqlMapClientTemplate().update("Param.updateBase", param);
    }
}
