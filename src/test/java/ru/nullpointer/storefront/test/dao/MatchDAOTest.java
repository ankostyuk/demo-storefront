package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.MatchDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.test.AssertUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class MatchDAOTest {

    private Logger logger = LoggerFactory.getLogger(MatchDAOTest.class);
    //
    @Resource
    private MatchDAO matchDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private RegionDAO regionDAO;

    @Test
    public void test_getCategoryMatchList_plainCategory() {
        Category cat = DAOTestHelper.getPlainCategory();
        Region region = null;

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Match> matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, region, null, null);

        assertNotNull(matchList);
        assertFalse(matchList.isEmpty());
        assertTrue(matchList.size() <= pageConfig.getPageSize());

        for (Match match : matchList) {
            assertNotNull(match);
            assertEquals(Match.Type.OFFER, match.getType());
            assertNotNull(match.getId());
        }
        checkSorting(matchList, sorting, region);
        checkModeration(matchList);

        // DESCENDING price
        sorting = MatchSorting.PRICE_DESCENDING;
        matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, region, null, null);

        checkSorting(matchList, sorting, region);
    }

    @Test
    public void test_getCategoryMatchList_parametrizedCategory_noFilter() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        Region region = null;

        ParamSetDescriptor psd = paramSetDAO.getParamSetDescriptorById(cat.getParameterSetDescriptorId());
        assertNotNull(psd);

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Match> matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, region, null, psd);

        assertNotNull(matchList);
        assertFalse(matchList.isEmpty());
        assertTrue(matchList.size() <= pageConfig.getPageSize());

        int offerCount = 0;
        int modelCount = 0;
        for (Match match : matchList) {
            assertNotNull(match);
            assertNotNull(match.getType());
            assertNotNull(match.getId());

            if (match.getType() == Match.Type.OFFER) {
                offerCount++;
            }
            if (match.getType() == Match.Type.MODEL) {
                modelCount++;
            }
        }
        assertTrue(offerCount > 0);
        assertTrue(modelCount > 0);

        checkSorting(matchList, sorting, region);
        checkModeration(matchList);
    }

    @Test
    public void test_getCategoryMatchCount_plainCategory() {
        Category cat = DAOTestHelper.getPlainCategory();

        int count = matchDAO.getMatchCount(cat.getId(), null, null, null);
        assertTrue(count > 0);
    }

    @Test
    public void test_getCategoryMatchCount_parametrizedCategory_noFilter() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        ParamSetDescriptor psd = paramSetDAO.getParamSetDescriptorById(cat.getParameterSetDescriptorId());

        int count = matchDAO.getMatchCount(cat.getId(), null, null, psd);
        assertTrue(count > 0);
    }

    @Test
    public void test_getModelInfoMap() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        ParamSetDescriptor psd = paramSetDAO.getParamSetDescriptorById(cat.getParameterSetDescriptorId());

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Match> matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, null, null, psd);
        Set<Integer> modelIdSet = new HashSet<Integer>();
        for (Match match : matchList) {
            if (match.getType() == Match.Type.MODEL) {
                modelIdSet.add(match.getId());
            }
        }
        assertFalse(modelIdSet.isEmpty());

        Map<Integer, ModelInfo> modeInfoMap = matchDAO.getModelInfoMap(modelIdSet, null);
        assertNotNull(modeInfoMap);
        assertTrue(modelIdSet.size() == modeInfoMap.size());

        for (Integer id : modelIdSet) {
            ModelInfo modelInfo = modeInfoMap.get(id);
            assertNotNull(modelInfo);
            assertEquals(id, modelInfo.getModelId());

            assertNotNull(modelInfo.getMinPrice());
            assertNotNull(modelInfo.getMaxPrice());
            assertTrue(modelInfo.getMinPrice().compareTo(modelInfo.getMaxPrice()) <= 0);

            assertTrue(modelInfo.getOfferCount() > 0);
        }
    }

    @Test
    public void test_getModelOfferIdList() {
        Model model = DAOTestHelper.getModelWithOffer();
        assertNotNull(model);

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Integer> idList = matchDAO.getModelOfferIdList(model.getId(), pageConfig, sorting, null, null);

        AssertUtils.assertUnique(idList);
        assertTrue(idList.size() <= pageConfig.getPageSize());
    }

    @Test
    public void test_getModelOfferIdList_inRegion() {
        Model model = DAOTestHelper.getModelWithOffer();
        assertNotNull(model);

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        Region region = regionDAO.getRegionsByNameText("Россия").get(0);

        List<Integer> idList = matchDAO.getModelOfferIdList(model.getId(), pageConfig, sorting, region, null);

        AssertUtils.assertUnique(idList);
        assertTrue(idList.size() <= pageConfig.getPageSize());
    }

    @Test
    public void test_getModelOfferCount() {
        Model model = DAOTestHelper.getModelWithOffer();
        assertNotNull(model);

        int count = matchDAO.getModelOfferCount(model.getId(), null, null);

        assertTrue(count > 0);
    }

    @Test
    public void test_getModelOfferCount_inRegion() {
        Model model = DAOTestHelper.getModelWithOffer();
        assertNotNull(model);

        Region region = regionDAO.getRegionsByNameText("Россия").get(0);
        int count = matchDAO.getModelOfferCount(model.getId(), region, null);

        Set<Integer> modelIdSet = new HashSet<Integer>();
        modelIdSet.add(model.getId());
        ModelInfo info = matchDAO.getModelInfoMap(modelIdSet, region).get(model.getId());
        assertNotNull(info);

        assertEquals(info.getOfferCount().intValue(), count);
    }

    @Test
    public void test_getAccessibleMatchSubset() {
        // WARNING: Используется предположение что в каталоге
        // есть модели и предложения с идентификатоми в пределах 0..10
        final int minId = 1;
        final int maxId = 10;

        // Только модели
        Set<Match> matchSet = new HashSet<Match>();
        for (int i = minId; i <= maxId; i++) {
            matchSet.add(new Match(Match.Type.MODEL, i));
        }

        Set<Match> accessibleMatchSet = matchDAO.getAccessibleMatchSubset(matchSet, null, false);

        assertNotNull(accessibleMatchSet);
        assertFalse(accessibleMatchSet.isEmpty());
        assertTrue(matchSet.size() >= accessibleMatchSet.size());
        for (Match m : accessibleMatchSet) {
            assertEquals(Match.Type.MODEL, m.getType());
            assertTrue(m.getId() >= minId && m.getId() <= maxId);
        }

        // Только предложения
        matchSet.clear();
        for (int i = minId; i <= maxId; i++) {
            matchSet.add(new Match(Match.Type.OFFER, i));
        }
        accessibleMatchSet = matchDAO.getAccessibleMatchSubset(matchSet, null, false);

        assertNotNull(accessibleMatchSet);
        assertFalse(accessibleMatchSet.isEmpty());
        assertTrue(matchSet.size() >= accessibleMatchSet.size());
        for (Match m : accessibleMatchSet) {
            assertEquals(Match.Type.OFFER, m.getType());
            assertTrue(m.getId() >= minId && m.getId() <= maxId);
        }

        // Модели и предложения
        matchSet.clear();
        for (int i = minId; i <= maxId; i++) {
            matchSet.add(new Match(Match.Type.MODEL, i));
            matchSet.add(new Match(Match.Type.OFFER, i));
        }
        accessibleMatchSet = matchDAO.getAccessibleMatchSubset(matchSet, null, false);

        assertNotNull(accessibleMatchSet);
        assertFalse(accessibleMatchSet.isEmpty());
        assertTrue(matchSet.size() >= accessibleMatchSet.size());
        for (Match m : accessibleMatchSet) {
            assertTrue(m.getId() >= minId && m.getId() <= maxId);
        }
    }

    @Test
    public void test_getAccessibleMatchSubset_inPlainCategory() {
        Category cat = DAOTestHelper.getPlainCategory();
        Region region = null;

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Match> matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, region, null, null);
        assertNotNull(matchList);
        assertFalse(matchList.isEmpty());

        Set<Match> accessibleMatchSet = matchDAO.getAccessibleMatchSubset(new HashSet<Match>(matchList), cat.getId(), false);
        assertNotNull(accessibleMatchSet);
        assertEquals(matchList.size(), accessibleMatchSet.size());

        Set<Integer> offerIdSet = new HashSet<Integer>();
        for (Match m : accessibleMatchSet) {
            assertTrue(matchList.contains(m));
            assertEquals(Match.Type.OFFER, m.getType());
            offerIdSet.add(m.getId());
        }

        for (Offer offer : offerDAO.getOfferMap(offerIdSet).values()) {
            assertEquals(cat.getId(), offer.getCategoryId());
            assertFalse(offer.isParametrized() || offer.isModelLinked());
        }
    }

    @Test
    public void test_getAccessibleMatchSubset_inParametrizedCategory() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        ParamSetDescriptor psd = paramSetDAO.getParamSetDescriptorById(cat.getParameterSetDescriptorId());
        Region region = null;

        PageConfig pageConfig = new PageConfig(1, 10);
        MatchSorting sorting = MatchSorting.PRICE_ASCENDING;

        List<Match> matchList = matchDAO.getMatchList(cat.getId(), pageConfig, sorting, region, null, psd);
        assertNotNull(matchList);
        assertFalse(matchList.isEmpty());

        Set<Match> accessibleMatchSet = matchDAO.getAccessibleMatchSubset(new HashSet<Match>(matchList), cat.getId(), true);
        assertNotNull(accessibleMatchSet);
        assertTrue(matchList.size() >= accessibleMatchSet.size());

        Set<Integer> offerIdSet = new HashSet<Integer>();
        Set<Integer> modelIdSet = new HashSet<Integer>();
        for (Match m : accessibleMatchSet) {
            assertTrue(matchList.contains(m));
            switch (m.getType()) {
                case OFFER:
                    offerIdSet.add(m.getId());
                    break;
                case MODEL:
                    modelIdSet.add(m.getId());
                    break;
                default:
                    fail("Неизвестный тип совпадения: " + m.getType());
            }
        }
        assertFalse(offerIdSet.isEmpty());
        assertFalse(modelIdSet.isEmpty());

        for (Offer offer : offerDAO.getOfferMap(offerIdSet).values()) {
            assertEquals(cat.getId(), offer.getCategoryId());
            assertTrue(offer.isParametrized() || offer.isModelLinked());
        }
        for (Model model : modelDAO.getModelMap(modelIdSet).values()) {
            assertEquals(cat.getId(), model.getCategoryId());
        }
    }

    private void checkSorting(List<Match> matchList, MatchSorting sorting, Region region) {
        if (!matchList.isEmpty()) {
            // Построить список цен
            List<BigDecimal> priceList = new ArrayList<BigDecimal>();
            BigDecimal price = null;
            for (Match match : matchList) {
                switch (match.getType()) {
                    case OFFER:
                        price = offerDAO.getOfferById(match.getId()).getUnitPrice();
                        break;
                    case MODEL:
                        Set<Integer> modelIdSet = new HashSet<Integer>();
                        modelIdSet.add(match.getId());
                        price = matchDAO.getModelInfoMap(modelIdSet, region).get(match.getId()).getMinPrice();
                        break;
                    default:
                        fail("Неизвестный тип совпадения");
                }
                priceList.add(price);
            }

            // Проверить сортировку
            BigDecimal previousPrice = priceList.get(0);
            for (int i = 1; i < priceList.size(); i++) {
                price = priceList.get(i);
                switch (sorting) {
                    case PRICE_ASCENDING:
                        assertTrue(previousPrice.compareTo(price) <= 0);
                        break;
                    case PRICE_DESCENDING:
                        assertTrue(previousPrice.compareTo(price) >= 0);
                        break;
                    default:
                        fail("Неизвестный тип сортировки");
                }
                previousPrice = price;
            }
        }
    }

    private void checkModeration(List<Match> matchList) {
        // TODO проверить модели

        Set<Integer> offerIdSet = new HashSet<Integer>();
        for (Match m : matchList) {
            if (m.getType() == Match.Type.OFFER) {
                offerIdSet.add(m.getId());
            }
        }

        Map<Integer, Offer> offerMap = offerDAO.getOfferMap(offerIdSet);
        for (Offer offer : offerMap.values()) {
            assertTrue(offer.getActive());
            assertEquals(Offer.Status.APPROVED, offer.getStatus());
        }
    }
}
