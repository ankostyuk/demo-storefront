package ru.nullpointer.storefront.dao.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.SettingsDAO;
import ru.nullpointer.storefront.domain.Settings;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisSettingsDAO extends SqlMapClientDaoSupport implements SettingsDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Settings getSettings(Integer id) {
        Assert.notNull(id);
        return (Settings) getSqlMapClientTemplate().queryForObject("Settings.selectById", id);
    }

    @Override
    public void insert(Settings settings) {
        Assert.notNull(settings);
        Assert.notNull(settings.getId());
        getSqlMapClientTemplate().insert("Settings.insert", settings);
    }

    @Override
    public void updateInfo(Settings settings) {
        Assert.notNull(settings);
        Assert.notNull(settings.getId());
        getSqlMapClientTemplate().update("Settings.updateInfo", settings, 1);
    }
}
