package ru.nullpointer.storefront.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.CurrencyRateDAO;
import ru.nullpointer.storefront.dao.ExtraCurrencyDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.CurrencyRate;
import ru.nullpointer.storefront.domain.ExtraCurrency;
import ru.nullpointer.storefront.service.support.CurrencyRateRetriever;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class CurrencyService {

    private Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    //
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private CurrencyRateDAO currencyRateDAO;
    @Resource
    private ExtraCurrencyDAO extraCurrencyDAO;
    @Resource
    private CurrencyRateRetriever currencyRateRetriever;
    @Resource
    private TimeService timeService;
    @Resource
    private SecurityService securityService;

    /**
     * Возвращает коэффициент на который надо домножить значение в основной валюте,
     * чтобы получить значение в дополнительной. 
     * Не использовать для вычисления величин хранящихся в базе (потеряется точность).
     * @param extraCurrency код дополнительной валюты
     * @return
     * @throws IllegalArgumentException если <code>extraCurrency</code> не является одной из дополнительных валют
     */
    @Transactional(readOnly = true)
    public BigDecimal getExtraCurrencyMultiplier(String extraCurrency) {
        Assert.isTrue(currencyConfig.getExtraCurrencyList().contains(extraCurrency));

        // TODO: cache
        CurrencyRate currencyRate = currencyRateDAO.getCurrencyRate(extraCurrency, currencyConfig.getDefaultCurrency());
        BigDecimal multiplier = currencyRate.getFromRate().divide(currencyRate.getToRate(), RoundingMode.HALF_UP);

        return multiplier;
    }

    /**
     * Возвращает коэффициент на который надо домножить значение в дополнительной валюте,
     * чтобы получить значение в основной. 
     * Не использовать для вычисления величин хранящихся в базе (потеряется точность).
     * @param extraCurrency код дополнительной валюты
     * @return
     * @throws IllegalArgumentException если <code>extraCurrency</code> не является одной из дополнительных валют
     */
    @Transactional(readOnly = true)
    public BigDecimal getDefaultCurrencyMultiplier(String extraCurrency) {
        Assert.isTrue(currencyConfig.getExtraCurrencyList().contains(extraCurrency));

        // TODO: cache
        CurrencyRate currencyRate = currencyRateDAO.getCurrencyRate(currencyConfig.getDefaultCurrency(), extraCurrency);
        BigDecimal multiplier = currencyRate.getFromRate().divide(currencyRate.getToRate(), RoundingMode.HALF_UP);

        return multiplier;
    }

    /**
     * Возвращает карту дополнительных валют поставщика.
     * @return Карту компаний. Ключем является <code>currency</code> - код дополнительной валюты. 
     *         Карта может быть пустой если поставщик использует курсы валют по умолчанию
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public Map<String, ExtraCurrency> getExtraCurrencyMap() {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        return extraCurrencyDAO.getExtraCurrencyMapByCompanyId(companyId);
    }

    /**
     * Сохраняет настройку дополнительной валюты поставщика.
     * Устанавливает владельцем настройки аутентифицированного поставщика.
     * @param extraCurrency
     * @throws IllegalArgumentException если <code>extraCurrency</code> или <code>extraCurrency.currency</code> равен <code>null</code>;
     *         если значение поля <code>extraCurrency.currency</code> не является одним из допустимых значений дополнительных валют;
     *         если значения полей <code>percent</code> и <code>fixedRate</code> противоречат значению <code>type</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional
    public void storeExtraCurrency(ExtraCurrency extraCurrency) {
        checkExtraCurrency(extraCurrency);

        Integer companyId = securityService.getAuthenticatedCompanyId();

        // тип отличен от настройки по-умолчанию
        if (extraCurrency.getType() != null) {
            // добавить
            extraCurrency.setCompanyId(companyId);
            extraCurrencyDAO.insert(extraCurrency);

            int count = offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), extraCurrency.getCurrency(), companyId, null);
            logger.debug("При сохранении настройки для {} обновлено {} предложений (unitPrice)", extraCurrency.getCurrency(), count);
        }
    }

    /**
     * Обновляет настройку дополнительной валюты поставщика.
     * Не меняет поставщика-владельца настройки.
     * @param extraCurrency настройки дополнительной валюты поставщика
     * @throws IllegalArgumentException если <code>extraCurrency</code> или <code>extraCurrency.currency</code> равен <code>null</code>;
     *         если значение поля <code>extraCurrency.currency</code> не является одним из допустимых значений дополнительных валют;
     *         если значения полей <code>percent</code> и <code>fixedRate</code> противоречат значению <code>type</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional
    public void updateExtraCurrencyInfo(ExtraCurrency extraCurrency) {
        checkExtraCurrency(extraCurrency);

        Integer companyId = securityService.getAuthenticatedCompanyId();
        Map<String, ExtraCurrency> ecMap = extraCurrencyDAO.getExtraCurrencyMapByCompanyId(companyId);
        ExtraCurrency storedEC = ecMap.get(extraCurrency.getCurrency());

        Assert.isTrue(storedEC != null && storedEC.getCompanyId().equals(companyId),
                "Попытка обновить несуществующую или чужую настройку дополнительной валюты");

        if (extraCurrency.getType() != null) {
            // обновить
            extraCurrencyDAO.updateInfo(extraCurrency);
        } else {
            // использовать значение по-умолчанию
            // удалить
            extraCurrencyDAO.delete(extraCurrency);
        }

        // пересчитать цены на товарные предложения поставщика, указанные в дополнительной валюте
        int count = offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), extraCurrency.getCurrency(), companyId, null);

        logger.debug("При обновлении настройки для {} обновлено {} предложений (unitPrice)", extraCurrency.getCurrency(), count);
    }

    /**
     * Обновляет курсы валют. Обновляет все цены за единицу измерения предложения.
     * Обновляеются только те предложения, цены на которые указаны в валюте отличной от основной валюты
     * @return
     */
    // TODO: Security
    @Transactional
    public boolean updateCurrencyRates() {
        String defaultCurrency = currencyConfig.getDefaultCurrency();
        List<String> extraList = currencyConfig.getExtraCurrencyList();
        Date now = timeService.now();

        List<CurrencyRate> currencyRateList = currencyRateRetriever.getRates(defaultCurrency, extraList, now);
        if (currencyRateList.isEmpty()) {
            logger.error("Не удалось получить котировки валют");
            return false;
        }
        if (currencyRateList.size() < extraList.size()) {
            logger.error("Котировки валют получены не полностью ({})", currencyRateList);
            return false;
        }

        for (CurrencyRate currencyRate : currencyRateList) {
            // обновить курс
            CurrencyRate contraRate = new CurrencyRate();
            contraRate.setFromCurrency(currencyRate.getToCurrency());
            contraRate.setFromRate(currencyRate.getToRate());
            contraRate.setToCurrency(currencyRate.getFromCurrency());
            contraRate.setToRate(currencyRate.getFromRate());

            currencyRateDAO.update(currencyRate);
            currencyRateDAO.update(contraRate);

            // пересчитать цены
            int count = offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), currencyRate.getFromCurrency(), null, null);

            logger.debug("Обновлено предложений (unitPrice): {}", count);
        }

        return true;
    }

    private void checkExtraCurrency(ExtraCurrency extraCurrency) {
        Assert.notNull(extraCurrency);
        Assert.notNull(extraCurrency.getCurrency());

        Assert.isTrue(currencyConfig.getExtraCurrencyList().contains(extraCurrency.getCurrency()));

        // обнулить поля соответственно типу настройки
        if (extraCurrency.getType() != null) {
            switch (extraCurrency.getType()) {
                case PERCENT:
                    Assert.notNull(extraCurrency.getPercent());
                    extraCurrency.setFixedRate(null);
                    break;
                case FIXED_RATE:
                    Assert.notNull(extraCurrency.getFixedRate());
                    extraCurrency.setPercent(null);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип настройки: " + extraCurrency.getType());
            }
        }
    }
}
