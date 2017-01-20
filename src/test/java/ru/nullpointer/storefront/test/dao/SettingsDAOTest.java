package ru.nullpointer.storefront.test.dao;

import ru.nullpointer.storefront.test.AssertUtils;
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
import ru.nullpointer.storefront.dao.SettingsDAO;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.SessionDescriptor;
import ru.nullpointer.storefront.domain.Settings;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class SettingsDAOTest {

    private Logger logger = LoggerFactory.getLogger(SettingsDAOTest.class);
    //
    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private SettingsDAO settingsDAO;

    @Test
    public void test_CRU() {
        SessionDescriptor sd = DAOTestHelper.createSession();

        Integer id = sd.getId();

        Settings s = new Settings();
        s.setId(id);
        s.setRegionAware(false);
        s.setRegionId(null);
        s.setExtraCurrency("USD");
        s.setPageSize(11);
        s.setPriceType(Settings.PRICE_TYPE.DEFAULT);

        // CREATE
        settingsDAO.insert(s);

        // READ
        Settings s2 = settingsDAO.getSettings(id);
        logger.debug("settings: {}", s2);
        assertNotNull(s2);
        assertSettingsEquals(s, s2);

        // UPDATE
        Region r = DAOTestHelper.getRegionByName("Москва");
        assertNotNull(r);

        s.setRegionId(r.getId());
        s.setRegionAware(true);
        s.setExtraCurrency("EUR");
        s.setPageSize(1001);
        s.setPriceType(Settings.PRICE_TYPE.EXTRA_CURRENCY);

        settingsDAO.updateInfo(s);

        s2 = settingsDAO.getSettings(id);
        logger.debug("settings: {}", s2);
        assertNotNull(s2);
        assertSettingsEquals(s, s2);

        Region r2 = s2.getRegion();
        assertNotNull(r2);
        assertEquals(r.getId(), r2.getId());
        assertEquals(r.getName(), r2.getName());
        assertEquals(r.getLeft(), r2.getLeft());
        assertEquals(r.getRight(), r2.getRight());

    }

    private void assertSettingsEquals(Settings s, Settings s2) {
        AssertUtils.assertPropertiesEquals(s, s2,
                "id", "regionAware", "regionId",
                "extraCurrency", "pageSize", "priceType");
    }
}
