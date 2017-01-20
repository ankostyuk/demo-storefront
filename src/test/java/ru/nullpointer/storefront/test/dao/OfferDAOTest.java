package ru.nullpointer.storefront.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Country;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.DateWindowConfig;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.StringCount;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class OfferDAOTest {

    private Logger logger = LoggerFactory.getLogger(OfferDAOTest.class);
    //
    private static final PageConfig CATEGORY_OFFER_PAGE_CONFIG = new PageConfig(1, 10000);
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private OfferTestHelper offerTestHelper;

    @Test
    public void tesGetCategoryOfferCount() {
        try {
            offerDAO.getCategoryOfferCount(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        Category cat = DAOTestHelper.getPlainCategory();
        int count = offerDAO.getCategoryOfferCount(cat.getId());
        assertTrue(count > 0);
    }

    @Test
    public void tesGetCategoryAccessibleOfferCount() {
        try {
            offerDAO.getCategoryAccessibleOfferCount(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        Category cat = DAOTestHelper.getPlainCategory();
        int count = offerDAO.getCategoryAccessibleOfferCount(cat.getId());
        assertTrue(count > 0);
    }

    @Test
    public void tesGetCategoryAccessibleModelOfferCount() {
        try {
            offerDAO.getCategoryAccessibleModelOfferCount(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        Category cat = DAOTestHelper.getPlainCategory();
        int count = offerDAO.getCategoryAccessibleModelOfferCount(cat.getId());
        assertEquals(0, count);

        cat = DAOTestHelper.getParametrizedCategory();
        count = offerDAO.getCategoryAccessibleModelOfferCount(cat.getId());
        assertTrue(count > 0);
    }

    @Test
    public void tesGetCategoryOfferPaginatedList() {
        Category cat = DAOTestHelper.getPlainCategory();

        try {
            offerDAO.getCategoryOfferPaginatedList(null, CATEGORY_OFFER_PAGE_CONFIG);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }
        try {
            offerDAO.getCategoryOfferPaginatedList(cat.getId(), null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        List<Offer> offerList = offerDAO.getCategoryOfferPaginatedList(cat.getId(), CATEGORY_OFFER_PAGE_CONFIG);
        assertFalse(offerList.isEmpty());
        assertTrue(offerList.size() <= CATEGORY_OFFER_PAGE_CONFIG.getPageSize());
        for (Offer offer : offerList) {
            assertOfferValid(offer);
            assertEquals(cat.getId(), offer.getCategoryId());
        }
    }

    @Test
    public void testGetCompanyOfferListAll() {
        Company com = getCompany();

        PageConfig pageConfig = new PageConfig(1, 10);
        CompanyOfferShowing showing = CompanyOfferShowing.ALL;
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);
        assertNotNull(offerList);
        assertFalse(offerList.isEmpty());
        assertTrue(offerList.size() <= pageConfig.getPageSize());

        for (Offer offer : offerList) {
            assertOfferValid(offer);
            assertEquals(com.getId(), offer.getCompanyId());
        }
    }

    @Test
    public void testGetCompanyOfferList() {
        Company com = getCompany();

        Set<Integer> categoryIdSet = new HashSet<Integer>();
        Category cat = DAOTestHelper.getPlainCategory();
        categoryIdSet.add(cat.getId());

        PageConfig pageConfig = new PageConfig(1, 10);
        CompanyOfferShowing showing = CompanyOfferShowing.ALL;
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), categoryIdSet, pageConfig, showing, sorting);
        for (Offer offer : offerList) {
            assertOfferValid(offer);

            assertEquals(com.getId(), offer.getCompanyId());
            assertTrue(categoryIdSet.contains(offer.getCategoryId()));
        }
    }

    @Test
    public void testGetCompanyOfferListSorting() {
        Company com = getCompany();

        PageConfig pageConfig = new PageConfig(1, 10);
        CompanyOfferShowing showing = CompanyOfferShowing.ALL;
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);

        // DATE_CREATED_ASCENDING
        Date prev_date = DateUtils.addYears(new Date(), -100);
        logger.debug("Previous date: {}", prev_date);

        for (Offer offer : offerList) {
            assertTrue(offer.getCreateDate().getTime() >= prev_date.getTime());
            prev_date = offer.getCreateDate();
        }

        // DATE_CREATED_DESCENDING
        sorting = CompanyOfferSorting.DATE_CREATED_DESCENDING;
        offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);
        prev_date = DateUtils.addYears(new Date(), 100);
        for (Offer offer : offerList) {
            assertTrue(offer.getCreateDate().getTime() <= prev_date.getTime());
            prev_date = offer.getCreateDate();
        }

        // DATE_EDITED_ASCENDING
        sorting = CompanyOfferSorting.DATE_EDITED_ASCENDING;
        offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);
        prev_date = DateUtils.addYears(new Date(), -100);
        for (Offer offer : offerList) {
            assertTrue(offer.getEditDate().getTime() >= prev_date.getTime());
            prev_date = offer.getEditDate();
        }

        // DATE_EDITED_DESCENDING
        sorting = CompanyOfferSorting.DATE_EDITED_DESCENDING;
        offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);
        prev_date = DateUtils.addYears(new Date(), 100);
        for (Offer offer : offerList) {
            assertTrue(offer.getEditDate().getTime() <= prev_date.getTime());
            prev_date = offer.getEditDate();
        }

        // NAME
        /*
        sorting = CompanyOfferSorting.NAME;
        globalOfferList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, sorting);
        String prev_name = "";
        for (Offer offer : globalOfferList) {
        logger.debug("Offer name: {}", offer.getName());
        assertTrue(offer.getName().compareTo(prev_name) >= 0);
        prev_name = offer.getName();
        }
         */
    }

    @Test
    public void testGetCompanyOfferListShowing() {
        Company com = getCompany();

        PageConfig pageConfig = new PageConfig(1, 10);
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, CompanyOfferShowing.REJECTED, sorting);
        assertFalse(offerList.isEmpty());
        for (Offer offer : offerList) {
            assertEquals(offer.getStatus(), Offer.Status.REJECTED);
        }

        offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, CompanyOfferShowing.INACTIVE, sorting);
        assertFalse(offerList.isEmpty());
        for (Offer offer : offerList) {
            assertFalse(offer.getActive());
        }
    }

    @Test
    public void testGetCompanyOfferCount() {
        Company com = getCompany();

        Set<Integer> categoryIdSet = new HashSet<Integer>();
        Category cat = DAOTestHelper.getPlainCategory();
        categoryIdSet.add(cat.getId());

        for (CompanyOfferShowing showing : CompanyOfferShowing.values()) {
            int count = offerDAO.getCompanyOfferCount(com.getId(), categoryIdSet, showing);
            assertTrue(count > 0);
        }
    }

    @Test
    public void test_OfferMap() {
        Offer offer = getOffer();

        Set<Integer> offerIdSet = new HashSet<Integer>();
        offerIdSet.add(offer.getId());

        Map<Integer, Offer> offerMap = offerDAO.getOfferMap(offerIdSet);

        assertNotNull(offerMap);
        assertTrue(offerMap.size() == 1);

        Offer o = offerMap.get(offer.getId());
        assertNotNull(o);
        assertEquals(offer.getId(), o.getId());
    }

    @Test
    public void testInsert() {
        Company com = getCompany();
        Category cat = DAOTestHelper.getPlainCategory();

        Offer offer = initOffer(cat, com);

        offerDAO.insert(offer);

        Integer id = offer.getId();

        assertNotNull(id);
        Offer o = offerDAO.getOfferById(id);
        assertNotNull(o);
        assertNotNull(o.getId());

        assertOffersEqualExceptId(offer, o);
    }

    @Test
    public void testUpdateInfo() {
        Offer offer = getOffer();
        Integer id = offer.getId();

        // проверить что ид поставщика НЕ меняется
        Integer companyId = offer.getCompanyId();
        // изменить ид поставщика
        offer.setCompanyId(-100);

        offer.setName("test_offer_update");
        offer.setDescription("test_description_update");
        offer.setParamDescription("test_param_description_update");

        Country country = DAOTestHelper.getCountry();
        offer.setOriginCountry(country.getAlpha2());

        offer.setPrice(new BigDecimal(10));
        offer.setUnitPrice(new BigDecimal(100));
        offer.setCurrency("CCC");
        offer.setRatio(new BigDecimal(20));

        Date createDate = new Date();
        Date actualDate = DateUtils.addMonths(createDate, 1);

        offer.setActualDate(actualDate);
        offer.setActive(true);
        offer.setAvailable(true);
        offer.setDelivery(true);
        offer.setCreateDate(createDate);
        offer.setEditDate(createDate);
        offer.setImage(null);

        offer.setStatus(Offer.Status.REJECTED);
        offer.setModerationStartDate(createDate);
        offer.setModerationEndDate(DateUtils.addDays(createDate, 1));

        List<Offer.Rejection> rejectionList = new ArrayList<Offer.Rejection>();
        rejectionList.add(Offer.Rejection.CAPITAL);
        rejectionList.add(Offer.Rejection.MISPRINT);
        offer.setRejectionList(rejectionList);

        offerDAO.updateInfo(offer);

        // восстановить ид поставщика
        offer.setCompanyId(companyId);

        Offer o = offerDAO.getOfferById(id);
        assertNotNull(o);
        assertNotNull(o.getId());
        assertEquals(offer.getId(), o.getId());

        assertOffersEqualExceptId(offer, o);
    }

    @Test
    public void testDelete() {
        Offer offer = getOffer();
        Integer id = offer.getId();

        offerDAO.delete(offer);

        assertNull(offerDAO.getOfferById(id));
    }

    @Test
    public void test_updateOffersUnitPrice_all() {
        String priceCurrency = "EUR";

        int count = offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), priceCurrency, null, null);
        assertTrue(count > 0);
    }

    @Test
    public void test_updateOffersUnitPrice_company() {
        Company com = getCompany();

        String priceCurrency = "EUR";

        int updated = offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), priceCurrency, com.getId(), null);
        assertTrue(updated > 0);

        logger.debug("updated: {}", updated);

        CompanyOfferShowing showing = CompanyOfferShowing.ALL;
        CompanyOfferSorting sorting = CompanyOfferSorting.DATE_CREATED_ASCENDING;

        int total = offerDAO.getCompanyOfferCount(com.getId(), null, showing);

        PageConfig pageConfig = new PageConfig(1, total);
        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, showing, sorting);

        int count = 0;
        for (Offer offer : offerList) {
            if (priceCurrency.equals(offer.getCurrency())) {
                count++;
                BigDecimal calculatedUnitPrice = offerTestHelper.calculateOfferUnitPrice(offer);

                logger.debug("id: {}, unitPrice: {}, calculated: {}", new Object[]{offer.getId(), offer.getUnitPrice(), calculatedUnitPrice});

                assertTrue(offer.getUnitPrice().compareTo(calculatedUnitPrice) == 0);
            }
        }
        assertEquals(updated, count);
    }

    @Test
    public void testGetCompanyOfferCountMap() {
        Company com = getCompany();

        Integer companyId = com.getId();

        Map<Integer, Integer> countMap = offerDAO.getCompanyOfferCountMap(companyId);
        logger.debug("countMap: {}", countMap);

        assertNotNull(countMap);
        assertFalse(countMap.isEmpty());

        Set<Integer> categoryIdSet = new HashSet<Integer>();

        for (Integer categoryId : countMap.keySet()) {
            assertNotNull(categoryId);

            categoryIdSet.clear();
            categoryIdSet.add(categoryId);

            logger.debug("categoryId: {}", categoryId);
            logger.debug("categoryIdSet: {}", categoryIdSet);

            int count = offerDAO.getCompanyOfferCount(companyId, categoryIdSet, CompanyOfferShowing.ALL);
            assertTrue(count > 0);

            assertEquals(Integer.valueOf(count), countMap.get(categoryId));
        }
    }

    @Test
    public void test_getModeratorPendingOfferCountMap() {
        Integer moderatorId = getOfferModeratorId();

        Map<Integer, Integer> countMap = offerDAO.getModeratorPendingOfferCountMap(moderatorId);
        assertNotNull(countMap);
        assertFalse(countMap.isEmpty());

        for (Integer categoryId : countMap.keySet()) {
            assertNotNull(categoryId);
            assertTrue(countMap.get(categoryId) > 0);
        }
    }

    @Test
    public void test_getModeratorOfferList() {
        Integer moderatorId = getOfferModeratorId();
        Integer categoryId = DAOTestHelper.getPlainCategory().getId();

        DateWindowConfig windowConfig = new DateWindowConfig(null, null, 10, true);

        // все
        List<Offer> list = offerDAO.getModeratorOfferList(moderatorId, categoryId, windowConfig, false);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.size() <= windowConfig.getLimit());

        Offer first = list.get(0);
        Offer last = list.get(list.size() - 1);

        // все кроме первого
        DateWindowConfig windowConfig2 = new DateWindowConfig(first.getModerationStartDate(), first.getId(), 10, true);
        List<Offer> list2 = offerDAO.getModeratorOfferList(moderatorId, categoryId, windowConfig2, false);
        assertNotNull(list2);
        assertEquals(list.size() - 1, list2.size());

        for (int i = 0; i < list2.size(); i++) {
            Offer o1 = list.get(i + 1);
            Offer o2 = list2.get(i);
            assertEquals(o1.getId(), o2.getId());
            assertOffersEqualExceptId(o1, o2);
        }

        // все кроме последнего
        windowConfig2 = new DateWindowConfig(last.getModerationStartDate(), last.getId(), 10, false);
        list2 = offerDAO.getModeratorOfferList(moderatorId, categoryId, windowConfig2, false);
        assertNotNull(list2);
        assertEquals(list.size() - 1, list2.size());
        for (int i = 0; i < list2.size(); i++) {
            Offer o1 = list.get(i);
            Offer o2 = list2.get(i);
            assertEquals(o1.getId(), o2.getId());
            assertOffersEqualExceptId(o1, o2);
        }
    }

    @Test
    public void test_removeParamSetRefs() {
        Category cat = DAOTestHelper.getPlainCategory();

        int count = offerDAO.removeParamSetRefs(cat.getId());
        assertTrue(count > 0);
    }

    @Test
    public void test_setModeratorRefs() {
        List<Integer> idList = accountDAO.getAccountIdListFromRole(Role.ROLE_MANAGER_MODERATOR_OFFER);

        Set<Integer> idSet = new HashSet<Integer>(idList);
        assertFalse(idSet.isEmpty());

        // Предполагается что есть нераспределенные среди модераторов предложения
        // раздать все не распределенные
        int count1 = offerDAO.setModeratorRefs(null, idSet);
        assertTrue(count1 > 0);

        // снять все ссылки
        Integer moderatorId = idSet.iterator().next();
        int count2 = offerDAO.setModeratorRefs(moderatorId, Collections.<Integer>emptySet());
        assertTrue(count2 > 0);
    }

    @Test
    public void test_getCatalogOfferPriceInterval() {
        NumberInterval interval = offerDAO.getCatalogOfferPriceInterval();
        assertNotNull(interval);
        assertNotNull(interval.getMin());
        assertNotNull(interval.getMax());
        assertTrue(interval.getMin().compareTo(interval.getMax()) <= 0);
    }

    @Test
    public void test_getCategoryOfferPriceInterval() {
        Category cat = DAOTestHelper.getPlainCategory();

        NumberInterval interval = offerDAO.getCategoryOfferPriceInterval(cat.getId());
        assertNotNull(interval);
        assertNotNull(interval.getMin());
        assertNotNull(interval.getMax());
        assertTrue(interval.getMin().compareTo(interval.getMax()) <= 0);
    }

    @Test
    public void test_getModeOfferPriceInterval() {
        Model model = DAOTestHelper.getModelWithOffer();

        NumberInterval interval = offerDAO.getModelOfferPriceInterval(model.getId());
        assertNotNull(interval);
        assertNotNull(interval.getMin());
        assertNotNull(interval.getMax());
        assertTrue(interval.getMin().compareTo(interval.getMax()) <= 0);
    }

    @Test
    public void test_getUnlinkedBrandNameList() {
        List<StringCount> brandNames = offerDAO.getUnlinkedBrandNameList();

        logger.debug("unlinked brands: {}", brandNames);

        assertNotNull(brandNames);
        assertFalse(brandNames.isEmpty());
    }

    @Test
    public void test_setBrandByName() {
        List<StringCount> brandNames = offerDAO.getUnlinkedBrandNameList();
        assertNotNull(brandNames);
        assertFalse(brandNames.isEmpty());


        Brand brand = DAOTestHelper.createBrand();
        StringCount brandName = brandNames.get(0);

        int count = offerDAO.setBrandByName(brandName.getValue(), brand.getId());

        assertTrue(count > 0);

        List<StringCount> newBrandNames = offerDAO.getUnlinkedBrandNameList();

        assertFalse(newBrandNames.contains(brandName));
        for (StringCount sc : newBrandNames) {
            assertTrue(brandNames.contains(sc));
        }
    }

    private Offer getOffer() {
        Company com = getCompany();

        PageConfig pageConfig = new PageConfig(1, 10);

        List<Offer> offerList = offerDAO.getCompanyOfferList(com.getId(), null, pageConfig, CompanyOfferShowing.ALL, CompanyOfferSorting.NAME);

        assertNotNull(offerList);
        assertFalse(offerList.isEmpty());

        Offer offer = offerList.get(0);
        assertNotNull(offer);

        return offer;
    }

    private Integer getOfferModeratorId() {
        Account acc = accountDAO.getAccountByEmail("manager2@example.com");
        assertNotNull(acc);
        return acc.getId();
    }

    private Company getCompany() {
        Account acc = accountDAO.getAccountByEmail("company1@example.com");
        assertNotNull(acc);

        Company com = companyDAO.getCompanyById(acc.getId());
        assertNotNull(com);

        return com;
    }

    private void assertOfferValid(Offer offer) {
        assertNotNull(offer);

        assertNotNull(offer.getId());
        assertNotNull(offer.getCategoryId());
        assertNotNull(offer.getCompanyId());

        assertNotNull(offer.getName());
        assertFalse(offer.getName().trim().isEmpty());

        assertNotNull(offer.getCurrency());
        assertNotNull(offer.getRatio());

        assertNotNull(offer.getPrice());
        assertNotNull(offer.getUnitPrice());

        assertNotNull(offer.getActualDate());

        assertNotNull(offer.getCreateDate());
        assertNotNull(offer.getEditDate());
    }

    private void assertOffersEqualExceptId(Offer offer, Offer o) {
        assertEquals(offer.getCategoryId(), o.getCategoryId());
        assertEquals(offer.getCompanyId(), o.getCompanyId());
        assertEquals(offer.getName(), o.getName());
        assertEquals(offer.getDescription(), o.getDescription());
        assertEquals(offer.getBrandName(), o.getBrandName());
        assertEquals(offer.getOriginCountry(), o.getOriginCountry());
        assertTrue(offer.getPrice().compareTo(o.getPrice()) == 0);
        assertEquals(offer.getCurrency(), o.getCurrency());
        assertTrue(offer.getRatio().compareTo(o.getRatio()) == 0);
        assertTrue(offer.getUnitPrice().compareTo(o.getUnitPrice()) == 0);
        assertEquals(offer.getActualDate(), o.getActualDate());
        assertEquals(offer.getActive(), o.getActive());
        assertEquals(offer.getAvailable(), o.getAvailable());
        assertEquals(offer.getDelivery(), o.getDelivery());
        assertEquals(offer.getCreateDate(), o.getCreateDate());
        assertEquals(offer.getEditDate(), o.getEditDate());
        assertEquals(offer.getImage(), o.getImage());
        assertEquals(offer.getBrandId(), o.getBrandId());
        assertEquals(offer.getParamSetId(), o.getParamSetId());
        assertEquals(offer.getModelId(), o.getModelId());
        assertEquals(offer.getParamDescription(), o.getParamDescription());

        assertEquals(offer.getStatus(), o.getStatus());
        assertEquals(offer.getModeratorId(), o.getModeratorId());
        assertEquals(offer.getModerationStartDate(), o.getModerationStartDate());
        assertEquals(offer.getModerationEndDate(), o.getModerationEndDate());
    }

    private Offer initOffer(Category cat, Company com) {
        Offer offer = new Offer();
        offer.setCategoryId(cat.getId());
        offer.setCompanyId(com.getId());
        offer.setName("test_offer");
        offer.setDescription("test_description");
        offer.setParamDescription("test_param_description");
        offer.setBrandName("test brand");
        offer.setModelName("test model");

        Country country = DAOTestHelper.getCountry();
        offer.setOriginCountry(country.getAlpha2());

        offer.setPrice(new BigDecimal(10));
        offer.setUnitPrice(new BigDecimal(100));
        offer.setCurrency("CCC");
        offer.setRatio(new BigDecimal(20));

        Date createDate = new Date();
        Date actualDate = DateUtils.addMonths(createDate, 1);

        offer.setActualDate(actualDate);
        offer.setActive(true);
        offer.setAvailable(true);
        offer.setDelivery(true);
        offer.setCreateDate(createDate);
        offer.setEditDate(createDate);
        offer.setImage(null);
        offer.setBrandId(DAOTestHelper.createBrand().getId());
        offer.setParamSetId(null);
        offer.setModelId(null);

        offer.setStatus(Offer.Status.PENDING);
        offer.setModeratorId(null);
        offer.setModerationStartDate(createDate);
        offer.setModerationEndDate(DateUtils.addDays(createDate, 1));
        offer.setRejectionMask(null);

        return offer;
    }
}
