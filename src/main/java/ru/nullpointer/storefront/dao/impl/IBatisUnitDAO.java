package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.UnitDAO;
import ru.nullpointer.storefront.domain.Unit;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisUnitDAO extends SqlMapClientDaoSupport implements UnitDAO {

    private Unit queryForObject(String select, Object param) {
        return (Unit) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private Map<Integer, Unit> queryForMap(String select, Object param, String property) {
        return (Map<Integer, Unit>) getSqlMapClientTemplate().queryForMap(select, param, property);
    }

    private List<Unit> queryForList(String select, Object param) {
        return (List<Unit>) getSqlMapClientTemplate().queryForList(select, param);
    }

    @Override
    public Map<Integer, Unit> getUnitMap(Set<Integer> unitIdSet) {
        Assert.notNull(unitIdSet);
        if (unitIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем
        // в список
        List<Integer> param = new ArrayList<Integer>(unitIdSet);
        return queryForMap("Unit.selectMapByIdSet", param, "id");
    }

    @Override
    public List<Unit> getAllUnits() {
        return queryForList("Unit.selectAll", null);
    }

    @Override
    public Unit getUnitById(Integer id) {
        Assert.notNull(id);
        return queryForObject("Unit.selectById", id);
    }

    @Override
    public List<Unit> findUnitListByName(String text) {
        Assert.notNull(text);
        return queryForList("Unit.findByName", text);
    }

    @Override
    public void addUnit(Unit unit) {
        Assert.notNull(unit);
        Assert.hasText(unit.getName());
        getSqlMapClientTemplate().insert("Unit.insert", unit);
    }

    @Override
    public void updateUnitInfo(Unit unit) {
        Assert.notNull(unit);
        Assert.notNull(unit.getId());
        Assert.hasText(unit.getName());
        getSqlMapClientTemplate().update("Unit.updateInfo", unit);
    }

    @Override
    public void deleteUnit(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Unit.delete", id, 1);
    }
}
