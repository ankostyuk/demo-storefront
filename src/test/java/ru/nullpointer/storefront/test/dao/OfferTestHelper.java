package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.CurrencyRateDAO;
import ru.nullpointer.storefront.dao.ExtraCurrencyDAO;
import ru.nullpointer.storefront.domain.CurrencyRate;
import ru.nullpointer.storefront.domain.ExtraCurrency;
import ru.nullpointer.storefront.domain.Offer;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class OfferTestHelper {

    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private CurrencyRateDAO currencyRateDAO;
    @Resource
    private ExtraCurrencyDAO extraCurrencyDAO;

    /**
     * Вычисляет и устанавливает цену за единицу измерения предложения в валюте по-умолчанию
     * @param offer
     */
    BigDecimal calculateOfferUnitPrice(Offer offer) {
        BigDecimal price = offer.getPrice().setScale(4, RoundingMode.HALF_UP);
        BigDecimal ratio = offer.getRatio();
        BigDecimal unitPrice;

        if (offer.getCurrency().equals(currencyConfig.getDefaultCurrency())) {
            // Основная валюта
            // unitPrice = price / ratio
            unitPrice = price.divide(ratio, RoundingMode.HALF_UP);
        } else {
            Quotient quot = calculatePriceMultiplier(offer.getCompanyId(), offer.getCurrency());
            // unitPrice = (price * dividend) / divisor / ratio
            unitPrice = price.multiply(quot.getDividend()).divide(quot.getDivisor(), RoundingMode.HALF_UP).divide(ratio, RoundingMode.HALF_UP);
        }
        return unitPrice;
    }

    private Quotient calculatePriceMultiplier(Integer companyId, String priceCurrency) {
        BigDecimal dividend;
        BigDecimal HUNDRED = new BigDecimal(100);

        CurrencyRate rate = currencyRateDAO.getCurrencyRate(priceCurrency, currencyConfig.getDefaultCurrency());

        Map<String, ExtraCurrency> ecMap = extraCurrencyDAO.getExtraCurrencyMapByCompanyId(companyId);
        ExtraCurrency extraCurrency = ecMap.get(priceCurrency);
        if (extraCurrency != null) {
            switch (extraCurrency.getType()) {
                case PERCENT:
                    // dividend = (100 + percent) * (toRate) / 100
                    dividend = (HUNDRED.add(extraCurrency.getPercent()))//
                            .multiply(rate.getToRate())//
                            .divide(HUNDRED, RoundingMode.HALF_UP);
                    break;
                case FIXED_RATE:
                    // !!! TODO: DOCUMENT fixedRate указывается относительно fromRate
                    // dividend = fixedRate
                    dividend = extraCurrency.getFixedRate();
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип настройки: " + extraCurrency.getType());
            }
        } else {
            // использовать курсы по-умолчанию
            // dividend = toRate
            dividend = rate.getToRate();
        }
        return new Quotient(dividend, rate.getFromRate());
    }

    private static class Quotient {

        private BigDecimal dividend;
        private BigDecimal divisor;

        Quotient(BigDecimal dividend, BigDecimal divisor) {
            this.dividend = dividend;
            this.divisor = divisor;
        }

        BigDecimal getDividend() {
            return dividend;
        }

        BigDecimal getDivisor() {
            return divisor;
        }
    }
}
