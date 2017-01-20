package ru.nullpointer.storefront.service;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.SettingsDAO;
import ru.nullpointer.storefront.domain.Settings;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class SettingsService {

    @Resource
    private SessionService sessionService;
    @Resource
    private SettingsDAO settingsDAO;
    @Resource
    private CurrencyService currencyService;
    @Resource
    private CurrencyConfig currencyConfig;

    @Transactional
    public Settings getSettings() {
        Settings settings = null;

        Integer sessionId = sessionService.getSessionId(false);
        if (sessionId != null) {
            settings = settingsDAO.getSettings(sessionId);
        }

        if (settings == null) {
            settings = createNewSettings();
        }

        if (settings.getPriceType() != Settings.PRICE_TYPE.DEFAULT) {
            String extraCurrency = settings.getExtraCurrency();
            settings.setExtraCurrencyMultiplier(currencyService.getExtraCurrencyMultiplier(extraCurrency));
        }

        return settings;
    }

    @Transactional
    public void updateSettings(Settings settings) {
        Assert.notNull(settings);

        if (settings.isRegionAware() && settings.getRegionId() == null) {
            settings.setRegionAware(false);
        }

        if (settings.getPriceType() == null) {
            settings.setPriceType(Settings.PRICE_TYPE.DEFAULT);
        }

        if (!currencyConfig.getExtraCurrencyList().contains(settings.getExtraCurrency())) {
            settings.setExtraCurrency(currencyConfig.getExtraCurrencyList().get(0));
        }

        if (settings.getPageSize() < 0 || settings.getPageSize() > Settings.MAX_PAGE_SIZE) {
            settings.setPageSize(Settings.DEFAULT_PAGE_SIZE);
        }

        Integer sessionId = sessionService.getSessionId(true);
        if (sessionId != null) {
            settings.setId(sessionId);
            if (settingsDAO.getSettings(sessionId) != null) {
                settingsDAO.updateInfo(settings);
            } else {
                settingsDAO.insert(settings);
            }
        }
    }

    private Settings createNewSettings() {
        Settings settings = new Settings();

        settings.setRegionId(null);
        settings.setRegionAware(false);
        settings.setPriceType(Settings.PRICE_TYPE.DEFAULT);
        settings.setExtraCurrency(currencyConfig.getExtraCurrencyList().get(0));
        settings.setPageSize(Settings.DEFAULT_PAGE_SIZE);

        return settings;
    }
}
