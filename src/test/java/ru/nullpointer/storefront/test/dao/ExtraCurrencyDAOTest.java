package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.ExtraCurrencyDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.ExtraCurrency;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ExtraCurrencyDAOTest {

    private Logger logger = LoggerFactory.getLogger(ExtraCurrencyDAOTest.class);
    //
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private ExtraCurrencyDAO ecDAO;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidGetExtraCurrencyMapByCompanyId() {
        ecDAO.getExtraCurrencyMapByCompanyId(null);
    }

    @Test
    public void testGetExtraCurrencyMapByCompanyId() {
        Company company = getCompany("company2@example.com");
        Map<String, ExtraCurrency> ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());
        assertNotNull(ecMap);

        logger.debug("ecMap: {}", ecMap);

        List<String> ecList = currencyConfig.getExtraCurrencyList();
        assertEquals(ecList.size(), ecMap.size());

        for (String cur : ecList) {
            ExtraCurrency extraCurrency = ecMap.get(cur);

            assertNotNull(extraCurrency);
            assertNotNull(extraCurrency.getId());
            assertEquals(company.getId(), extraCurrency.getCompanyId());
            assertEquals(cur, extraCurrency.getCurrency());

            ExtraCurrency.Type type = extraCurrency.getType();
            assertNotNull(type);

            switch (type) {
                case PERCENT:
                    assertNotNull(extraCurrency.getPercent());
                    assertTrue(extraCurrency.getPercent().compareTo(BigDecimal.ZERO) >= 0);

                    assertNull(extraCurrency.getFixedRate());
                    break;

                case FIXED_RATE:
                    assertNotNull(extraCurrency.getFixedRate());
                    assertTrue(extraCurrency.getFixedRate().compareTo(BigDecimal.ZERO) > 0);

                    assertNull(extraCurrency.getPercent());
                    break;

                default:
                    fail();
            }
        }
    }

    @Test
    public void testEmptyMap() {
        Map<String, ExtraCurrency> ecMap = ecDAO.getExtraCurrencyMapByCompanyId(-100);
        assertNotNull(ecMap);
        assertTrue(ecMap.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInsert() {
        ecDAO.insert(null);
    }

    @Test
    public void testInsert() {
        Company company = getCompany("company1@example.com");

        List<ExtraCurrency> ecList = new ArrayList<ExtraCurrency>();
        for (int i = 0; i < currencyConfig.getCurrencyList().size(); i++) {
            String cur = currencyConfig.getCurrencyList().get(i);

            ExtraCurrency ec = new ExtraCurrency();
            ec.setCompanyId(company.getId());
            ec.setCurrency(cur);

            if (i % 2 == 0) {
                ec.setPercent(new BigDecimal(5));
            } else {
                ec.setFixedRate(new BigDecimal(50));

            }
            ecList.add(ec);
        }

        for (ExtraCurrency ec : ecList) {
            ecDAO.insert(ec);
            assertNotNull(ec.getId());
        }

        Map<String, ExtraCurrency> ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());
        for (ExtraCurrency ec : ecList) {
            ExtraCurrency ec1 = ecMap.get(ec.getCurrency());
            assertNotNull(ec1);

            assertEquals(ec.getId(), ec1.getId());
            assertEquals(ec.getCompanyId(), ec1.getCompanyId());
            assertEquals(ec.getCurrency(), ec1.getCurrency());
            if (ec.getPercent() != null) {
                assertTrue(ec.getPercent().compareTo(ec1.getPercent()) == 0);
            } else {
                assertTrue(ec.getFixedRate().compareTo(ec1.getFixedRate()) == 0);
            }
        }
    }

    @Test
    public void testInvalidUpdateInfo() {
        try {
            ecDAO.updateInfo(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            ecDAO.updateInfo(new ExtraCurrency());
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testUpdateInfo() {
        Company company = getCompany("company2@example.com");

        Integer companyId = company.getId();
        
        List<ExtraCurrency> ecList = new ArrayList<ExtraCurrency>();
        Map<String, ExtraCurrency> ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());
        logger.debug("update ecMap: {}", ecMap);

        for (String cur : ecMap.keySet()) {
            ExtraCurrency ec = ecMap.get(cur);
            if (ec.getPercent() != null) {
                ec.setPercent(null);
                ec.setFixedRate(new BigDecimal(100));
            } else {
                ec.setFixedRate(null);
                ec.setPercent(new BigDecimal(10));
            }
            // Проверить что не меняется ид поставщика
            ec.setCompanyId(-100);
            
            ecDAO.updateInfo(ec);
            ecList.add(ec);
        }

        ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());
        logger.debug("update ecMap: {}", ecMap);

        assertEquals(ecList.size(), ecMap.size());

        for (ExtraCurrency ec : ecList) {
            ExtraCurrency ec1 = ecMap.get(ec.getCurrency());
            logger.debug("currency: {}", ec.getCurrency());
            assertNotNull(ec1);

            assertEquals(ec.getId(), ec1.getId());
            assertEquals(companyId, ec1.getCompanyId());
            assertEquals(ec.getCurrency(), ec1.getCurrency());
            if (ec.getPercent() != null) {
                assertTrue(ec.getPercent().compareTo(ec1.getPercent()) == 0);
            } else {
                assertTrue(ec.getFixedRate().compareTo(ec1.getFixedRate()) == 0);
            }
        }
    }

    @Test
    public void testInvalidDelete() {
        try {
            ecDAO.delete(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            ecDAO.delete(new ExtraCurrency());
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testDelete() {
        Company company = getCompany("company2@example.com");
        Map<String, ExtraCurrency> ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());

        for (String cur : ecMap.keySet()) {
            ExtraCurrency ec = ecMap.get(cur);
            ecDAO.delete(ec);
        }

        ecMap = ecDAO.getExtraCurrencyMapByCompanyId(company.getId());
        assertTrue(ecMap.isEmpty());
    }

    private Company getCompany(String email) {
        Account account = accountDAO.getAccountByEmail(email);
        assertNotNull(account);

        Company company = companyDAO.getCompanyById(account.getId());
        assertNotNull(company);
        return company;
    }
}
