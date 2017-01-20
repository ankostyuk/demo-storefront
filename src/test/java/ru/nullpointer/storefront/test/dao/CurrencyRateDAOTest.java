package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import javax.annotation.Resource;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.CurrencyRateDAO;
import ru.nullpointer.storefront.domain.CurrencyRate;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CurrencyRateDAOTest {

    @Resource
    private CurrencyRateDAO currencyRateDAO;
    @Resource
    private CurrencyConfig currencyConfig;

    @Test
    public void testGetCurrencyRate() {
        for (String fromCurrency : currencyConfig.getExtraCurrencyList()) {
            assertHasCurrencyRate(fromCurrency, currencyConfig.getDefaultCurrency());
        }

        for (String toCurrency : currencyConfig.getExtraCurrencyList()) {
            assertHasCurrencyRate(currencyConfig.getDefaultCurrency(), toCurrency);
        }
    }

    @Test
    public void testInvalidGetCurrencyRate() {
        assertNull(currencyRateDAO.getCurrencyRate("XXX", "YYY"));

        for (String currency : currencyConfig.getCurrencyList()) {
            assertNull(currencyRateDAO.getCurrencyRate(currency, currency));
        }

        try {
            currencyRateDAO.getCurrencyRate(null, null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testUpdate() {
        BigDecimal x = new BigDecimal("13.0101");
        BigDecimal y = new BigDecimal("17.0109");
        for (String fromCurrency : currencyConfig.getExtraCurrencyList()) {
            String toCurrency = currencyConfig.getDefaultCurrency();

            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setFromCurrency(fromCurrency);
            currencyRate.setToCurrency(toCurrency);

            currencyRate.setFromRate(x);
            currencyRate.setToRate(y);

            currencyRateDAO.update(currencyRate);

            currencyRate = currencyRateDAO.getCurrencyRate(fromCurrency, toCurrency);

            assertTrue(currencyRate.getFromRate().compareTo(x) == 0);
            assertTrue(currencyRate.getToRate().compareTo(y) == 0);
        }

        for (String toCurrency : currencyConfig.getExtraCurrencyList()) {
            String fromCurrency = currencyConfig.getDefaultCurrency();

            CurrencyRate currencyRate = new CurrencyRate();
            currencyRate.setFromCurrency(fromCurrency);
            currencyRate.setToCurrency(toCurrency);

            currencyRate.setFromRate(x);
            currencyRate.setToRate(y);

            currencyRateDAO.update(currencyRate);

            currencyRate = currencyRateDAO.getCurrencyRate(fromCurrency, toCurrency);

            assertTrue(currencyRate.getFromRate().compareTo(x) == 0);
            assertTrue(currencyRate.getToRate().compareTo(y) == 0);
        }

    }

    @Test
    public void testInvalidUpdate() {
        try {
            currencyRateDAO.update(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            CurrencyRate cr = new CurrencyRate();
            currencyRateDAO.update(cr);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            CurrencyRate cr = new CurrencyRate();
            cr.setFromCurrency(currencyConfig.getDefaultCurrency());
            currencyRateDAO.update(cr);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    private void assertHasCurrencyRate(String from, String to) {
        CurrencyRate currencyRate = currencyRateDAO.getCurrencyRate(from, to);
        assertNotNull(currencyRate);

        assertNotNull(currencyRate.getFromRate());
        assertTrue(currencyRate.getFromRate().compareTo(BigDecimal.ONE) >= 0);

        assertNotNull(currencyRate.getToRate());
        assertTrue(currencyRate.getToRate().compareTo(BigDecimal.ONE) >= 0);

        assertEquals(from, currencyRate.getFromCurrency());
        assertEquals(to, currencyRate.getToCurrency());
    }
}
