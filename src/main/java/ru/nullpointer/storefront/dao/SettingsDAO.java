package ru.nullpointer.storefront.dao;

import ru.nullpointer.storefront.domain.Settings;

/**
 *
 * @author Alexander Yastrebov
 */
public interface SettingsDAO {

    Settings getSettings(Integer id);

    void insert(Settings settings);

    void updateInfo(Settings settings);
}
