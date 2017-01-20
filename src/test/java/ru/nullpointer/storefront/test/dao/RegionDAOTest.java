package ru.nullpointer.storefront.test.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class RegionDAOTest {

    private Logger logger = LoggerFactory.getLogger(RegionDAOTest.class);
    //
    private static final PageConfig FIND_REGION_PAGE_CONFIG = new PageConfig(1, 10000);
    @Resource
    private RegionDAO regionDAO;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    @Rollback(true)
    public void testMethodGetRegionByNameText_Locale() {
        List<Region> regions = null;
        int size = 0;
        logger.debug("TEST: SQL LOWER() - кириллица");
        regions = regionDAO.getRegionsPaginatedByNameText("Москва", FIND_REGION_PAGE_CONFIG);
        size = regions.size();
        assertTrue(size > 0);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
        regions = regionDAO.getRegionsPaginatedByNameText("москва", FIND_REGION_PAGE_CONFIG);
        assertTrue(regions.size() == size);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
        regions = regionDAO.getRegionsPaginatedByNameText("МОСКВА", FIND_REGION_PAGE_CONFIG);
        assertTrue(regions.size() == size);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
        regions = regionDAO.getRegionsPaginatedByNameText("мОСКВА", FIND_REGION_PAGE_CONFIG);
        assertTrue(regions.size() == size);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
        regions = regionDAO.getRegionsPaginatedByNameText("мОсКвА", FIND_REGION_PAGE_CONFIG);
        assertTrue(regions.size() == size);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
    }

    @Test
    @Rollback(true)
    public void testMethodGetRegionsByNameText() {
        List<Region> regions = null;

        logger.debug("TEST: invalid arguments");
        try {
            regionDAO.getRegionsByNameText(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            regionDAO.getRegionsByNameText("");
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Отсутствие вхождения текста в наименовании регионов");
        regions = regionDAO.getRegionsByNameText("$$$Региона с таким именем не существует$$$");
        assertTrue(regions.isEmpty());

        logger.debug("TEST: Наличие вхождения текста в наименовании регионов");
        regions = regionDAO.getRegionsByNameText("оск"); // "М<оск>ва"
        assertTrue(!regions.isEmpty());
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
    }

    @Test
    @Rollback(true)
    public void testMethodGetRegionsPaginatedByNameText() {
        List<Region> regions = null;

        logger.debug("TEST: invalid arguments");
        try {
            regionDAO.getRegionsPaginatedByNameText(null, FIND_REGION_PAGE_CONFIG);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            regionDAO.getRegionsPaginatedByNameText("", FIND_REGION_PAGE_CONFIG);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            regionDAO.getRegionsPaginatedByNameText("$$$Регион с таким именем существует$$$", null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Отсутствие вхождения текста в наименовании регионов");
        regions = regionDAO.getRegionsPaginatedByNameText("$$$Региона с таким именем не существует$$$", FIND_REGION_PAGE_CONFIG);
        assertTrue(regions.isEmpty());

        logger.debug("TEST: Наличие вхождения текста в наименовании регионов");
        regions = regionDAO.getRegionsPaginatedByNameText("оск", FIND_REGION_PAGE_CONFIG); // "М<оск>ва"
        assertTrue(!regions.isEmpty());
        assertTrue(regions.size() <= FIND_REGION_PAGE_CONFIG.getPageSize());
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
    }

    @Test
    @Rollback(true)
    public void testMethodGetRegionsByNameTextCount() {
        int count = 0;

        logger.debug("TEST: null arguments");
        try {
            regionDAO.getRegionsByNameTextCount(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            regionDAO.getRegionsByNameTextCount("");
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Отсутствие вхождения текста в наименовании регионов");
        count = regionDAO.getRegionsByNameTextCount("$$$Региона с таким именем не существует$$$");
        assertTrue(count == 0);

        logger.debug("TEST: Наличие вхождения текста в наименовании регионов");
        count = regionDAO.getRegionsByNameTextCount("москва"); // "М<оск>ва"
        assertTrue(count == 2);
    }

    @Test
    @Rollback(true)
    public void testMethodGetRegionPath() {
        Region region = new Region();
        List<Region> regions = null;

        logger.debug("TEST: null & no valid arguments");
        try {
            regionDAO.getRegionPath(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            region.setLeft(null);
            regionDAO.getRegionPath(region);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            region.setRight(null);
            regionDAO.getRegionPath(region);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            region.setLeft(1);
            region.setRight(1);
            regionDAO.getRegionPath(region);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: регион 1-го уровня (Россия)");
        region.setLeft(1);
        region.setRight(5460);
        regions = regionDAO.getRegionPath(region);
        assertTrue(regions.isEmpty());

        logger.debug("TEST: регион 4-го уровня (Москва)");
        region.setLeft(4);
        region.setRight(7);
        regions = regionDAO.getRegionPath(region);
        assertTrue(regions.size() == 3);
        for (Region r : regions) {
            logger.debug("TEST: Регион={}", r);
        }
    }

    @Test
    public void testGetCompanyDeliveryRegionList() {
        Company company = getCompany("company1@example.com");

        Integer companyId = company.getId();
        List<Region> deliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);

        assertNotNull(deliveryList);
        assertFalse(deliveryList.isEmpty());
    }

    @Test
    public void testInvalidGetCompanyDeliveryRegionList() {
        List<Region> deliveryList = regionDAO.getCompanyDeliveryRegionList(-100);

        assertNotNull(deliveryList);
        assertTrue(deliveryList.isEmpty());
    }

    @Test
    public void testInsertCompanyDeliveryRegion() {
        Company company = getCompany("company1@example.com");

        Integer companyId = company.getId();
        List<Region> deliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);

        Region region = DAOTestHelper.getRegionByName("Владивосток");
        assertNotNull(region);

        regionDAO.insertCompanyDeliveryRegion(companyId, region.getId());

        List<Region> newDeliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);
        assertEquals(deliveryList.size() + 1, newDeliveryList.size());

        boolean success = false;
        for (Region r : newDeliveryList) {
            if (r.getId().equals(region.getId())) {
                success = true;
                break;
            }
        }

        if (!success) {
            fail();
        }
    }

    @Test
    public void testDeleteCompanyDeliveryRegion() {
        Company company = getCompany("company1@example.com");

        Integer companyId = company.getId();
        List<Region> deliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);
        assertTrue(deliveryList.size() > 0);

        Region region = deliveryList.get(0);

        regionDAO.deleteCompanyDeliveryRegion(companyId, region.getId());

        List<Region> newDeliveryList = regionDAO.getCompanyDeliveryRegionList(companyId);
        assertEquals(deliveryList.size() - 1, newDeliveryList.size());

        boolean success = true;
        for (Region r : newDeliveryList) {
            if (r.getId().equals(region.getId())) {
                success = false;
                break;
            }
        }

        if (!success) {
            fail();
        }
    }

    @Test
    public void test_getRegionMap() {
        Set<Integer> regionIdSet = new HashSet<Integer>();
        regionIdSet.add(getCompany("company1@example.com").getRegionId());
        regionIdSet.add(getCompany("company2@example.com").getRegionId());
        regionIdSet.add(getCompany("company3@example.com").getRegionId());

        Map<Integer, Region> regionMap = regionDAO.getRegionMap(regionIdSet);
        assertNotNull(regionMap);
        assertEquals(regionIdSet.size(), regionMap.size());
        for (Integer regionId : regionIdSet) {
            Region region = regionMap.get(regionId);
            assertNotNull(region);
            assertEquals(regionId, region.getId());
        }
    }

    private Company getCompany(String email) {
        Account account = accountDAO.getAccountByEmail(email);
        assertNotNull(account);

        Company company = companyDAO.getCompanyById(account.getId());
        assertNotNull(company);
        return company;
    }
}
