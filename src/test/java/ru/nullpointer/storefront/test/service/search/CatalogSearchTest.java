package ru.nullpointer.storefront.test.service.search;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilterClause;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Brand;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.SearchLifecycle;
import ru.nullpointer.storefront.service.search.catalog.CatalogSearcher;
import ru.nullpointer.storefront.service.search.Fragment;
import ru.nullpointer.storefront.service.search.FragmentScoreComparator;
import ru.nullpointer.storefront.service.search.IdentityFieldValue;
import ru.nullpointer.storefront.service.search.RegionFilter;
import ru.nullpointer.storefront.service.search.SearchConfig;
import ru.nullpointer.storefront.service.search.SearchHighlighter;
import ru.nullpointer.storefront.service.search.SearchResult;
import ru.nullpointer.storefront.service.search.Type;
import ru.nullpointer.storefront.test.service.support.ServiceTestHelper;

/**
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
//@TransactionConfiguration
//@Transactional
public class CatalogSearchTest {

    private Logger logger = LoggerFactory.getLogger(CatalogSearchTest.class);
    //

    //
    private static final boolean SKIP_CREATE_INDEX_TEST = false;
    private static final boolean SKIP_INDEX_TEST = false;
    //

    private static final int RESULT_COUNT_DEFAULT = 100;

    private static final SearchConfig SEARCH_CONFIG_DEFAULT =
            new SearchConfig(RESULT_COUNT_DEFAULT, null, SearchUtils.SORT_RELEVANCE, null);

    private static final SearchConfig SEARCH_CONFIG_SORT_TYPE_ORDER =
            new SearchConfig(RESULT_COUNT_DEFAULT, null, SearchUtils.SORT_TYPE_ORDER, null);

    private static final SearchConfig SEARCH_CONFIG_SORT_INDEX_ORDER =
            new SearchConfig(RESULT_COUNT_DEFAULT, null, SearchUtils.SORT_INDEX_ORDER, null);

    @Resource
    private ServiceTestHelper serviceTestHelper;
    @Resource
    private SearchTestHelper searchTestHelper;

    @Resource
    private CatalogService catalogService;
    @Resource
    private OfferService offerService;
    @Resource
    private ModelService modelService;
    @Resource
    private BrandService brandService;
    @Resource
    private UnitService unitService;
    
    @Resource
    private RegionDAO regionDAO;

    @Resource
    private SearchUtils searchUtils;
    @Resource
    private SearchLifecycle searchLifecycle;
    @Resource
    private CatalogSearcher catalogSearcher;
    @Resource
    private SearchHighlighter searchHighlighter;

    @Test
    public void testCreateIndex() throws Exception {
        if (SKIP_CREATE_INDEX_TEST) return;
        
        logger.debug("TEST createIndex start");
        searchLifecycle.createCatalogIndex();
        //
        searchTestHelper.waitingForCompleteTasks();
        //
        int docCount = searchLifecycle.getCatalogIndexDocCount();
        logger.debug("TEST createIndex finish, indexed doc count={}", docCount);
        searchTestHelper.testConformityIndex();
    }

    @Test
    public void testCatalogItemsIndex() throws Exception {
        if (SKIP_INDEX_TEST) return;

        serviceTestHelper.authenticateAsManagerCatalog();

        //
        boolean oldPooling = searchLifecycle.isPooling();
        searchLifecycle.setPooling(false);
        //
        //searchLifecycle.createIndex();
        //
        // catalogSearchTestHelper.waitingForCompleteTasks();
        //
        List<CatalogItem> items = catalogService.getChildrenList(null);
        CatalogItem testSection = items.get(items.size() - 1);
        Unit unit = unitService.getAllUnits().get(0);

        // add section
        CatalogItem sectionItem = new CatalogItem();
        sectionItem.setName("Section");
        sectionItem.setType(CatalogItem.Type.SECTION);
        catalogService.addSection(sectionItem, null);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // add category 1
        CatalogItem categoryItem_1 = new CatalogItem();
        categoryItem_1.setName("Category_1");
        categoryItem_1.setType(CatalogItem.Type.CATEGORY);
        Category category_1 = new Category();
        category_1.setUnitId(unit.getId());
        catalogService.addCategory(categoryItem_1, category_1, sectionItem.getId());

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // add category 2
        CatalogItem categoryItem_2 = new CatalogItem();
        categoryItem_2.setName("Category_2");
        categoryItem_2.setType(CatalogItem.Type.CATEGORY);
        Category category_2 = new Category();
        category_2.setUnitId(unit.getId());
        catalogService.addCategory(categoryItem_2, category_2, sectionItem.getId());

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // update section
        sectionItem.setName("Section_update");
        sectionItem.setActive(true);
        catalogService.updateSectionInfo(sectionItem);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
        //
        sectionItem.setActive(false);
        catalogService.updateSectionInfo(sectionItem);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
        //
        sectionItem.setActive(true);
        catalogService.updateSectionInfo(sectionItem);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // update category 1
        categoryItem_1.setName("Category_1_update");
        categoryItem_1.setActive(false);
        catalogService.updateCategoryInfo(categoryItem_1, category_1);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // update category 2
        categoryItem_2.setName("Category_2_update");
        categoryItem_2.setActive(false);
        catalogService.updateCategoryInfo(categoryItem_2, category_2);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // update category 1, 2
        categoryItem_1.setActive(true);
        categoryItem_2.setActive(true);
        catalogService.updateCategoryInfo(categoryItem_1, category_1);
        catalogService.updateCategoryInfo(categoryItem_2, category_2);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // active section
        sectionItem.setActive(false);
        catalogService.updateSectionTreeActive(sectionItem);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
        //
        sectionItem.setActive(true);
        catalogService.updateSectionTreeActive(sectionItem);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // active category 1, 2
        categoryItem_1.setActive(false);
        categoryItem_2.setActive(false);
        catalogService.updateCategoryActive(categoryItem_1);
        catalogService.updateCategoryActive(categoryItem_2);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        categoryItem_1.setActive(true);
        categoryItem_2.setActive(true);
        catalogService.updateCategoryActive(categoryItem_1);
        catalogService.updateCategoryActive(categoryItem_2);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // changeParentItem
        //testSection.setActive(false);
        //catalogService.updateSectionInfo(testSection);
        catalogService.changeParentItem(sectionItem, testSection.getId());

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // delete category 1
        catalogService.deleteCategory(categoryItem_1);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // delete category 2
        catalogService.deleteCategory(categoryItem_2);

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        // delete section
        catalogService.deleteSection(sectionItem.getId());

        // catalogSearchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        //
        //searchLifecycle.createIndex();
        //
        searchLifecycle.setPooling(oldPooling);
        //
    }

    @Test
    public void testCatalogItemActivePool() throws Exception {
        if (SKIP_INDEX_TEST) return;
        
        serviceTestHelper.authenticateAsManagerCatalog();

        boolean oldPooling = searchLifecycle.isPooling();
        searchLifecycle.setPooling(true);
        activeCatalogItemTest(searchLifecycle.isPooling(), false);
        //searchLifecycle.setPooling(false);
        //activeCatalogItemTest(searchLifecycle.isPooling(), true);
        searchLifecycle.setPooling(oldPooling);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
    }

    @Test
    public void testOfferIndex() {
        if (SKIP_INDEX_TEST) return;

        serviceTestHelper.authenticateAsCompany();

        Offer offerCopy = serviceTestHelper.getExistOffer();
        offerCopy.setName("COPY OFFER");

        offerCopy.setActive(true);
        offerService.storeOffer(offerCopy, null);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
        
        offerCopy.setActive(false);
        offerService.updateOfferInfo(offerCopy, null);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        offerCopy.setName("RENAME " + offerCopy.getName());
        offerCopy.setActive(true);
        offerService.updateOfferInfo(offerCopy, null);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        offerService.deleteOffer(offerCopy);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
    }

    @Test
    public void testModelIndex() {
        if (SKIP_INDEX_TEST) return;

        serviceTestHelper.authenticateAsManagerModel();

        Map<Integer, Object> paramValues = Collections.emptyMap();

        Model modelCopy = serviceTestHelper.getExistModel();
        modelCopy.setName("COPY MODEL");

        modelService.storeModel(modelCopy, paramValues);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        modelCopy.setName("RENAME " + modelCopy.getName());
        modelService.updateModelInfo(modelCopy, paramValues);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        modelService.deleteModel(modelCopy);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
    }

    @Test
    public void testBrandIndex() {
        if (SKIP_INDEX_TEST) return;

        serviceTestHelper.authenticateAsManagerBrand();

        Brand brand = new Brand();
        brand.setName("TEST BRAND");

        brandService.storeBrand(brand);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        brand.setKeywords("тест бренд");
        brandService.updateBrandInfo(brand);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();

        brandService.deleteBrand(brand);
        searchTestHelper.waitingForCompleteTasks();
        searchTestHelper.testConformityIndex();
    }

    @Test
    public void testSearch() throws Exception {
        assertEquals(1, search("ручки", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, search("ручка", SearchUtils.SORT_RELEVANCE));
        //
        assertEquals(1, search("руч*", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, search("Межкомнатн*", SearchUtils.SORT_RELEVANCE));
        //
        //assertEquals(2, templateSearch("э"));
        assertTrue(templateSearch("э") >= 2);
    }

    @Test
    public void testDeepSearch() throws Exception {
        assertEquals(2, deepSearch("    двер    ", SearchUtils.SORT_RELEVANCE));
        assertEquals(2, deepSearch("двер", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("межкомнатн", SearchUtils.SORT_RELEVANCE));

        assertEquals(1, deepSearch("   Межкомнатная        две   ", SearchUtils.SORT_RELEVANCE));

        assertEquals(1, deepSearch("межкомнатная дверь", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("межкомнатная двери", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("межкомнатные дв", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("межкомнатные дверь", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("межкомнатные двери", SearchUtils.SORT_RELEVANCE));

        assertEquals(1, deepSearch("двери межкомнат", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("дверь межкомнат", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("двери межкомнатная", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("дверь межкомнатная", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("двери межкомнатные", SearchUtils.SORT_RELEVANCE));

        assertEquals(1, deepSearch("дверь меж", SearchUtils.SORT_RELEVANCE));

        assertEquals(0, deepSearch("две меж", SearchUtils.SORT_RELEVANCE));
        
        assertEquals(1, deepSearch("makita dcs 7300-45", SearchUtils.SORT_RELEVANCE));
        assertEquals(1, deepSearch("makita dcs 7300-4", SearchUtils.SORT_RELEVANCE));
        assertEquals(2, deepSearch("makita dcs 7300-", SearchUtils.SORT_RELEVANCE));
        assertEquals(2, deepSearch("makita dcs 7300", SearchUtils.SORT_RELEVANCE));
    }

    @Test
    public void testFuzzySearch() throws Exception {
        String expected;

        //
        expected = "Ручки";

        testFuzzySearchDoc("ручки", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руки", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руска", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("ручк", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руччк", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("учка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("учк", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("cучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("hучка", expected, SearchUtils.SORT_RELEVANCE);

        testFuzzySearchDoc("оучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("пучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("нучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("иучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("тучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("мучка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("ркчка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("рвчуа", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("пцчка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руяка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("рувка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руыка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("ркчка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("рцчка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("рчка", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("рука", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("руча", expected, SearchUtils.SORT_RELEVANCE);

        //
        expected = "Замки";

        testFuzzySearchDoc("Замки", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("заки", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("амки", expected, SearchUtils.SORT_RELEVANCE);
        // testFuzzySearchDoc("мки", expected, SearchUtils.SORT_RELEVANCE); // TODO ?
        testFuzzySearchDoc("амк", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("хамки", expected, SearchUtils.SORT_RELEVANCE);

        //
        expected = "Двери";

        testFuzzySearchDoc("двирь", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("жвери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("даери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("дыери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("дчери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("дуери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двкри", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двнри", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двпри", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двеои", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двепи", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двени", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двеги", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("двкрм", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("дверт", expected, SearchUtils.SORT_RELEVANCE);

        //
        expected = "Межкомнатные двери";

        testFuzzySearchDoc("межкомнатные двери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("мекомнатные двери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("мекомнатные вери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("мишкомнотные двири", expected, SearchUtils.SORT_RELEVANCE);

        testFuzzySearchDoc("Мжкомнатные двери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Межкмнатные двери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Межкомнатные дивери", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Мешкомнатаи двери", expected, SearchUtils.SORT_RELEVANCE);

        testFuzzySearchDoc("мишкомнотная", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Пежкомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("сежкомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("иекомнатнын", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("мнкомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("медкомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межеомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межуомнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межкрмнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межклмнатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Межкоинатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межкоснатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Межкомеатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межкомгатные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межкомнаьные", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("Межкомнатнве", expected, SearchUtils.SORT_RELEVANCE);
        testFuzzySearchDoc("межкомнатняе", expected, SearchUtils.SORT_RELEVANCE);

        testFuzzySearchDoc("межкомнатняеsfgsgs", expected, SearchUtils.SORT_RELEVANCE);

        testFuzzySearchDoc("   двирь     мишкомнотная       ", expected, SearchUtils.SORT_RELEVANCE);
    }

    @Test
    public void testEscapeSpecialCharacters() throws Exception {
        String spaces = "  \f\n\r\t   \f \n \r \t  ";
        String luceneSpecialCharacters = "+-&|!(){}[]^\"~*?:\\";
        String luceneReserveWords = "AND OR NOT {A TO Z}";
        String text = null;

        text = spaces;
        catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = spaces + luceneSpecialCharacters + spaces;
        catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = spaces + luceneSpecialCharacters + "text" + luceneSpecialCharacters + spaces;
        catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = luceneSpecialCharacters;
        catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = luceneReserveWords;
        catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //

        for (int i = 0; i < luceneSpecialCharacters.length(); i++) {
            String c = luceneSpecialCharacters.substring(i, i + 1);
            text = c;
            logger.debug("TEST text='{}'", text);
            catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
            catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
            //
            text = c + " text";
            logger.debug("TEST text='{}'", text);
            catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
            catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
            //
            text = "text " + c;
            logger.debug("TEST text='{}'", text);
            catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
            catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
            //
            text = "text " + c + " text";
            logger.debug("TEST text='{}'", text);
            catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT);
            catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        }

        String expected = "Межкомнатные двери";

        text = "межкомнатные двери";
        assertEquals(1, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        //
        text = luceneSpecialCharacters + "межкомнатные двери";
        assertEquals(1, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = "межкомнатные двери " + luceneSpecialCharacters;
        assertEquals(1, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = "межкомнатные " + luceneSpecialCharacters + "двери";
        assertEquals(1, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = luceneReserveWords + " межкомнатные двери";
        assertEquals(0, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = "межкомнатные двери " + luceneReserveWords;
        assertEquals(0, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
        //
        text = "межкомнатные " + luceneReserveWords + "двери";
        assertEquals(0, deepSearch(text, SearchUtils.SORT_RELEVANCE));
        catalogSearcher.fuzzySearchContents(text, SEARCH_CONFIG_DEFAULT);
    }

    @Test
    public void testSortTypeOrder() throws Exception {
        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_SORT_TYPE_ORDER).getList();
            searchTestHelper.testTypeOrder(docs);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_SORT_TYPE_ORDER).getList();
            searchTestHelper.testTypeOrder(docs);
        }
    }
    
    @Test
    public void testPaging() throws Exception {
        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            doPaging(c.toString());
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            doPaging(c.toString());
        }
    }

    @Test
    public void testFiltering() throws Exception {
        boolean findDifferentTypeDocs = false;
        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_SORT_INDEX_ORDER).getList();
            if (searchTestHelper.isDifferentTypeDocs(docs)) {
                findDifferentTypeDocs = true;
                doFiltering(text, docs);
            }
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_SORT_INDEX_ORDER).getList();
            if (searchTestHelper.isDifferentTypeDocs(docs)) {
                findDifferentTypeDocs = true;
                doFiltering(text, docs);
            }
        }
        assertTrue(findDifferentTypeDocs);
    }

    @Test
    public void testRegionFiltering() throws Exception {
        Company company = serviceTestHelper.getExistCompany();
        Region region = regionDAO.getRegionById(company.getRegionId());
        assertNotNull(region);

        logger.debug("TEST region filtering, region = {}", region);

        //
        logger.debug("TEST region filtering: ONLY REGION");

        Filter filter = searchUtils.buildRegionFilter(region);

        SearchConfig searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                SearchUtils.SORT_INDEX_ORDER, filter);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            logDocumentList(docs);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            logDocumentList(docs);
        }

        //
        logger.debug("TEST region filtering:  MODEL, OFFER & REGION");

        BooleanFilter booleanFilter = new BooleanFilter();
        booleanFilter.add(new FilterClause(SearchUtils.buildTypeFilter(new Type[]{Type.MODEL, Type.OFFER}), Occur.MUST));
        booleanFilter.add(new FilterClause(filter, Occur.MUST));

        filter = booleanFilter;

        searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                SearchUtils.SORT_INDEX_ORDER, filter);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            logDocumentList(docs);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            logDocumentList(docs);
        }

    }

    @Test
    public void testRegionSort() throws Exception {
        Company company = serviceTestHelper.getExistCompany();
        Region region = regionDAO.getRegionById(company.getRegionId());
        assertNotNull(region);

        logger.debug("TEST region sort, region = {}", region);

        boolean matchFirst = true;

        //
        logger.debug("TEST region sort: ONLY REGION");

        SearchConfig searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                new Sort(searchUtils.buildRegionMatchSortField(region, matchFirst)),
                null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            assertRegionSort(docs, region, matchFirst);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            assertRegionSort(docs, region, matchFirst);
        }

        //
        logger.debug("TEST region sort: REGION & TYPE");

        searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                new Sort(
                    new SortField[]{searchUtils.buildRegionMatchSortField(region, matchFirst),
                    SearchUtils.SORT_FIELD_TYPE_ORDER}),
                null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            assertRegionSort(docs, region, matchFirst);
            //catalogSearchTestHelper.testTypeOrder(docs);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            assertRegionSort(docs, region, matchFirst);
            //catalogSearchTestHelper.testTypeOrder(docs);
        }
    }

    @Test
    public void testPriceSort() throws Exception {
        //
        logger.debug("TEST price sort: ONLY PRICE ASC");

        SearchConfig searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                new Sort(SearchUtils.SORT_FIELD_PRICE_ASC),
                null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkPriceSort(docs, true);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkPriceSort(docs, true);
        }

        //
        logger.debug("TEST price sort: ONLY PRICE DESC");

        searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                new Sort(SearchUtils.SORT_FIELD_PRICE_DESC),
                null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkPriceSort(docs, false);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkPriceSort(docs, false);
        }

        //
        logger.debug("TEST price sort: REGION & (PRICE ASC)");

        Company company = serviceTestHelper.getExistCompany();
        Region region = regionDAO.getRegionById(company.getRegionId());
        assertNotNull(region);
        logger.debug("TEST price sort, region = {}", region);

        searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                    new Sort(
                        new SortField[]{searchUtils.buildRegionMatchSortField(region, true),
                        SearchUtils.SORT_FIELD_PRICE_ASC}),
                    null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkRegionPriceSort(docs, region, true, true);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkRegionPriceSort(docs, region, true, true);
        }

        //
        logger.debug("TEST price sort: REGION & (PRICE DESC)");

        logger.debug("TEST price sort, region = {}", region);

        searchConfig = new SearchConfig(
                RESULT_COUNT_DEFAULT, null,
                    new Sort(
                        new SortField[]{searchUtils.buildRegionMatchSortField(region, true),
                        SearchUtils.SORT_FIELD_PRICE_DESC}),
                    null);

        for (Character c : searchTestHelper.getCyrillicAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkRegionPriceSort(docs, region, true, false);
        }
        for (Character c : searchTestHelper.getLatinAlphabet()) {
            String text = c.toString();
            List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            //logDocumentList(docs);
            checkRegionPriceSort(docs, region, true, false);
        }
    }

    @Test
    public void testPriceFiltering() throws Exception {
        Double minPrice = 4567.89D;
        Double maxPrice = minPrice;

        while (minPrice >= 0D) {

            logger.debug("TEST price filtering, price: [{}] - [{}]", minPrice, maxPrice);

            Filter filter = searchUtils.buildPriceFilter(new BigDecimal(minPrice), new BigDecimal(maxPrice));

            //
            logger.debug("TEST price filtering: ONLY PRICE");

            SearchConfig searchConfig = new SearchConfig(
                    RESULT_COUNT_DEFAULT, null,
                    SearchUtils.SORT_INDEX_ORDER,
                    filter);

            for (Character c : searchTestHelper.getCyrillicAlphabet()) {
                String text = c.toString();
                List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
                //logDocumentList(docs);
                checkPriceFiltering(docs, minPrice, maxPrice);
            }
            for (Character c : searchTestHelper.getLatinAlphabet()) {
                String text = c.toString();
                List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
                //logDocumentList(docs);
                checkPriceFiltering(docs, minPrice, maxPrice);
            }

            //
            logger.debug("TEST price & type filtering + sort REGION & (PRICE ASC)");

            Type[] types = new Type[]{Type.MODEL, Type.OFFER};

            BooleanFilter booleanFilter = new BooleanFilter();
            booleanFilter.add(new FilterClause(SearchUtils.buildTypeFilter(types), Occur.MUST));
            booleanFilter.add(new FilterClause(filter, Occur.MUST));
            filter = booleanFilter;

            Company company = serviceTestHelper.getExistCompany();
            Region region = regionDAO.getRegionById(company.getRegionId());
            assertNotNull(region);
            logger.debug("TEST price & type filtering & sort, region = {}", region);

            searchConfig = new SearchConfig(
                    RESULT_COUNT_DEFAULT, null,
                        new Sort(
                            new SortField[]{searchUtils.buildRegionMatchSortField(region, true),
                            SearchUtils.SORT_FIELD_PRICE_ASC}),
                        filter);

            for (Character c : searchTestHelper.getCyrillicAlphabet()) {
                String text = c.toString();
                List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
                //logDocumentList(docs);
                checkPriceFiltering(docs, minPrice, maxPrice);
                checkRegionPriceSort(docs, region, true, true);
                checkDocType(docs, types);
            }
            for (Character c : searchTestHelper.getLatinAlphabet()) {
                String text = c.toString();
                List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
                //logDocumentList(docs);
                checkPriceFiltering(docs, minPrice, maxPrice);
                checkRegionPriceSort(docs, region, true, true);
                checkDocType(docs, types);
            }

            minPrice -= 234.56D;
            maxPrice += 234.56D;
        }
    }

    @Test
    public void testBestFragments() throws Exception {
        if (true) return;

        String expected;

        expected = "двери";
        doBestFragments("  двирь  ", expected);

        expected = "межкомнатные двери";
        doBestFragments("   мишкомнотная  двирь       ", expected);
        doBestFragments("   двирь     мишкомнотная       ", expected);

        expected = "межкомнатные";
        doBestFragments("мишкомнотная", expected);

        expected = "makita";
        doBestFragments("moaki", expected);

        expected = null;
        doBestFragments("moaki 123 XXX 15-2a", expected); // TODO ?

        expected = "Makita 6271DWPE";
        doBestFragments("Makito X271DWXX", expected);

        expected = "Makita 6260 DWPE";
        doBestFragments("Makit 626X DWXX", expected);

        expected = "Makita 6260";
        doBestFragments("Makit 626X", expected);
        // Makita 6260 DWPE 9,6V NiCd
        // makita dcs 7300-4
    }

    @Test
    public void testChars() throws Exception {
        if (true) return;

        serviceTestHelper.authenticateAsManagerCatalog();

        String[] texts = {
            "XX-34_\"YY\"'ZZ'«WW»(55){66}[77]+AA&CC|DD!EE^FF~GG*HH?II:JJ\\KK",
            "XX-+AA&CC|DD!EE^FF~GG*HH?II:JJ\\KK34_\"YY\"'ZZ'«WW»(55){66}[77]",
            "XX-1111(555){666}[777]@AA#CC$DD%EE@AAA#CCC$DDD%EEE-1234567890",
            "XX-@AA#CC$DD%EE@AAA#CCC$DDD%EEE-12345678901111(555){666}[777]"
        };

        // add section
        CatalogItem sectionItem = new CatalogItem();
        sectionItem.setName("TEST CHARS");
        sectionItem.setType(CatalogItem.Type.SECTION);
        catalogService.addSection(sectionItem, null);

        logger.debug("TEST testChars");

        List<Document> docs = null;

        for (int s = 0; s < texts.length; s++) {
            String text = texts[s];

            sectionItem.setName(text);
            sectionItem.setActive(true);
            catalogService.updateSectionInfo(sectionItem);

            searchTestHelper.waitingForCompleteTasks();

            for (int i = text.length(); i > 1; i--) {
                logger.debug("TEST text={}", text);
                docs = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_DEFAULT).getList();
                for (Document doc : docs) {
                    logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
                }
                assertEquals(1, docs.size());
                IdentityFieldValue identity = searchUtils.buildSectionIdentityFieldValue(sectionItem.getId());
                assertEquals(identity.getValue(), docs.get(0).get(SearchUtils.FIELD_IDENTITY));

                text = text.substring(0, text.length() - 1);
            }
        }


        // delete section
        catalogService.deleteSection(sectionItem.getId());

        searchTestHelper.waitingForCompleteTasks();
    }

    private void assertRegionSort(List<Document> docs, Region region, boolean matchFirst) {
        if (!docs.isEmpty()) {
            String regionText = docs.get(0).get(SearchUtils.FIELD_REGION);
            boolean match = regionText == null ? false : RegionFilter.isMatch(regionText, region);
            int count = 0;
            for (int i = 1; i < docs.size(); i++) {
                regionText = docs.get(i).get(SearchUtils.FIELD_REGION);
                boolean m = regionText == null ? false : RegionFilter.isMatch(regionText, region);
                if (m != match) {
                    match = m;
                    count++;
                }
            }
            assertTrue(count <= 1);
            if (count == 1) {
                assertTrue(matchFirst != match);
            }
        }
    }

    private void checkPriceSort(List<Document> docs, boolean asc) {
        if (!docs.isEmpty()) {
            double d = asc ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            for (Document doc : docs) {
                String priceText = doc.get(SearchUtils.FIELD_PRICE_MIN);
                if (priceText != null) {
                    double v = Double.parseDouble(priceText);
                    assertTrue(asc ? v >= d : v <= d);
                    d = v;
                }
            }
        }
    }

    private void checkRegionPriceSort(List<Document> docs, Region region, boolean matchFirst, boolean priceAsc) {
        if (!docs.isEmpty()) {
            String regionText = docs.get(0).get(SearchUtils.FIELD_REGION);
            boolean match = regionText == null ? false : RegionFilter.isMatch(regionText, region);
            int count = 0;

            String priceText = docs.get(0).get(SearchUtils.FIELD_PRICE_MIN);
            double d = (priceText == null ? (priceAsc ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY) : Double.parseDouble(priceText));

            for (int i = 1; i < docs.size(); i++) {
                regionText = docs.get(i).get(SearchUtils.FIELD_REGION);
                boolean m = regionText == null ? false : RegionFilter.isMatch(regionText, region);
                if (m != match) {
                    match = m;
                    count++;
                    d = priceAsc ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                }
                
                priceText = docs.get(i).get(SearchUtils.FIELD_PRICE_MIN);
                if (priceText != null) {
                    double v = Double.parseDouble(priceText);
                    assertTrue(priceAsc ? v >= d : v <= d);
                    d = v;
                }
            }
            assertTrue(count <= 1);
            if (count == 1) {
                assertTrue(matchFirst != match);
            }
        }
    }

    private void checkPriceFiltering(List<Document> docs, Double minPrice, Double maxPrice) {
        if (!docs.isEmpty()) {
            for (Document doc : docs) {
                Double min = null;
                String priceMinText = doc.get(SearchUtils.FIELD_PRICE_MIN);
                if (priceMinText != null) {
                    min = Double.parseDouble(priceMinText);
                }
                Double max = null;
                String priceMaxText = doc.get(SearchUtils.FIELD_PRICE_MAX);
                if (priceMinText != null) {
                    max = Double.parseDouble(priceMaxText);
                }
                if (min != null && max != null) {
                    assertTrue(min <= max);

                    assertTrue(minPrice <= max);
                    assertTrue(maxPrice >= min);
                }
            }
        }
    }

    private void checkDocType(List<Document> docs, Type[] types) {
        if (!docs.isEmpty()) {
            for (Document doc : docs) {
                Type type = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY)).getType();
                assertNotNull(type);
                boolean match = false;
                for (Type t : types) {
                    if (type.equals(t)) {
                        match = true;
                        break;
                    }
                }
                assertTrue(match);
            }
        }
    }

    private void doBestFragments(String text, String expected) throws Exception {
        logger.debug("TEST testBestFragments, text={}", text);

        SearchConfig searchConfig = SEARCH_CONFIG_DEFAULT;
        searchConfig.setResultCount(1);

        SearchResult<Document> result = catalogSearcher.fuzzySearchContents(text, searchConfig);
        List<Document> docs = result.getList();

        for (Document doc : docs) {
            logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
        }

        if (docs.isEmpty()) {
            assertNull(expected);
            return;
        }

        StringBuilder contents = new StringBuilder(docs.get(0).get(SearchUtils.FIELD_CONTENTS));
        for (int i = 1; i < docs.size(); i++) {
            contents.append(" ").append(docs.get(i).get(SearchUtils.FIELD_CONTENTS));
        }
//        String contents = docs.get(0).get(SearchUtils.FIELD_CONTENTS);
        logger.debug("TEST contents={}", contents);

        assertNotNull(result.getQuery());
        assertNotNull(result.getAnalyzer());

        List<Fragment> fragments = searchHighlighter.getBestFragments(text,
                SearchUtils.FIELD_CONTENTS, contents.toString(), result.getQuery(), result.getAnalyzer());

        assertFalse(fragments.isEmpty());

        Collections.sort(fragments, new FragmentScoreComparator());

        for (Fragment fragment : fragments) {
            logger.debug("TEST fragment={}", fragment);
        }

        assertTrue(expected.equalsIgnoreCase(fragments.get(fragments.size() - 1).getText()));
    }

    private void doFiltering(String text, List<Document> noFilteringDocs) throws Exception {
        Filter filter = null;
        SearchConfig searchConfig = null;
        List<Document> filteringDocs = null;

        Type filteringType = null;

        // type
        filteringType = new IdentityFieldValue(noFilteringDocs.get(0).get(SearchUtils.FIELD_IDENTITY)).getType();

        filter = SearchUtils.buildTypeFilter(filteringType);

        searchConfig = new SearchConfig(
                noFilteringDocs.size(), null,
                SearchUtils.SORT_INDEX_ORDER, filter);

        filteringDocs = catalogSearcher.deepSearchContents(text, searchConfig).getList();

        assertFalse(filteringDocs.isEmpty());

        for (Document doc : filteringDocs) {
            Type type = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY)).getType();
            assertEquals(filteringType, type);
        }

        // type & catalog item
        Set<Integer> catalogItemIdSet = new HashSet<Integer>();

        for (Document doc : noFilteringDocs) {
            String catalogItemIdValues = doc.get(SearchUtils.FIELD_CATALOG_ITEMS);

            if (catalogItemIdValues != null) {
                String[] catalogItemIds = catalogItemIdValues.split(" ");
                for (String id : catalogItemIds) {
                    catalogItemIdSet.add(Integer.parseInt(id));
                }
            }
        }

        for (Integer itemId : catalogItemIdSet) {
            BooleanFilter booleanFilter = new BooleanFilter();
            booleanFilter.add(new FilterClause(SearchUtils.buildTypeFilter(filteringType), Occur.MUST));
            booleanFilter.add(new FilterClause(searchUtils.buildCatalogItemFilter(itemId), Occur.MUST));

            filter = booleanFilter;

            searchConfig = new SearchConfig(
                    noFilteringDocs.size(), null,
                    SearchUtils.SORT_INDEX_ORDER, filter);

            filteringDocs = catalogSearcher.deepSearchContents(text, searchConfig).getList();

            for (Document doc : filteringDocs) {
                Type type = new IdentityFieldValue(doc.get(SearchUtils.FIELD_IDENTITY)).getType();
                assertEquals(filteringType, type);

                String catalogItemIdValues = doc.get(SearchUtils.FIELD_CATALOG_ITEMS);

                assertNotNull(catalogItemIdValues);
                assertTrue(catalogItemIdValues.trim().length() > 0);

                Set<Integer> idSet = new HashSet<Integer>();
                String[] catalogItemIds = catalogItemIdValues.split(" ");
                for (String id : catalogItemIds) {
                    idSet.add(Integer.parseInt(id));
                }

                assertTrue(idSet.contains(itemId));
            }
        }
    }

    private void doPaging(String text) throws Exception {
        SearchResult<Document> result = catalogSearcher.deepSearchContents(text, SEARCH_CONFIG_SORT_INDEX_ORDER);
        List<Document> expectedDocs = result.getList();

        int pageSize = expectedDocs.size() > 2 ? 2 : 1;
        int resultCount = expectedDocs.size();

        List<Document> paginatedDocs = null;
        PageConfig pageConfig = new PageConfig(1, pageSize);

        int pageCount = (result.getTotal() / pageSize) + (result.getTotal() % pageSize > 0 ? 1 : 0);

        int count = 0;
        int docsCount = 0;

        SearchConfig searchConfig = new SearchConfig(
                resultCount, pageConfig, SearchUtils.SORT_INDEX_ORDER, null);
        
        do {
            count++;

            paginatedDocs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
            docsCount += paginatedDocs.size();
            
            for (int i = 0; i < paginatedDocs.size(); i++) {
                Document doc = paginatedDocs.get(i);
                
                Document expectedDoc = expectedDocs.get((count - 1)*pageSize + i);
                searchTestHelper.testEqualsDoc(expectedDoc, doc);
            }

            pageConfig = new PageConfig(pageConfig.getPageNumber() + 1, pageConfig.getPageSize());
            searchConfig.setPageConfig(pageConfig);
        } while (pageConfig.getPageNumber() <= pageCount);

        assertTrue(pageCount == count || pageCount == count - 1);
        assertEquals(expectedDocs.size(), docsCount);
    }

    private void activeCatalogItemTest(boolean pooling, boolean updateActiveOnly) throws Exception {

        CatalogItem section = catalogService.getSubTree(null).get(0);

        // save active
        boolean active = section.getActive();

        for (int i = 0; i < 10; i++) {
            logger.debug("count={}", i);
            section.setActive(!section.getActive());
            if (updateActiveOnly) {
                catalogService.updateSectionTreeActive(section);
            } else {
                catalogService.updateSectionInfo(section);
            }
            if (pooling) {
                searchTestHelper.waitingForCompleteTasks();
            }

            searchTestHelper.testConformityIndex();
        }

        // restore active
        section.setActive(active);
        if (updateActiveOnly) {
            catalogService.updateSectionTreeActive(section);
        } else {
            catalogService.updateSectionInfo(section);
        }
        if (pooling) {
            searchTestHelper.waitingForCompleteTasks();
        }
    }

    private int search(String queryString, Sort sort) throws Exception {
        logger.debug("TEST searchContents start, queryString='{}'", queryString);
        SearchConfig searchConfig = new SearchConfig(RESULT_COUNT_DEFAULT, null, sort, null);
        List<Document> docs = catalogSearcher.searchContents(queryString, searchConfig).getList();
        logger.debug("TEST searchContents finish, searched doc count={}", docs.size());
        for (Document doc : docs) {
            logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
        }
        int docCount = docs.size();
        assertEquals(docCount, catalogSearcher.searchContents(queryString.toLowerCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.searchContents(queryString.toUpperCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.searchContents(searchTestHelper.toVariableCase(queryString), searchConfig).getList().size());
        return docCount;
    }

    private int deepSearch(String text, Sort sort) throws Exception {
        logger.debug("TEST deepSearchContents start, text='{}'", text);
        SearchConfig searchConfig = new SearchConfig(RESULT_COUNT_DEFAULT, null, sort, null);
        List<Document> docs = catalogSearcher.deepSearchContents(text, searchConfig).getList();
        logger.debug("TEST deepSearchContents finish, searched doc count={}", docs.size());
        for (Document doc : docs) {
            logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
        }
        int docCount = docs.size();
        assertEquals(docCount, catalogSearcher.deepSearchContents(text.toLowerCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.deepSearchContents(text.toUpperCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.deepSearchContents(searchTestHelper.toVariableCase(text), searchConfig).getList().size());
        return docCount;
    }

    private void testFuzzySearchDoc(String text, String expected, Sort sort) throws Exception {
        logger.debug("TEST fuzzySearchContents start, text='{}'", text);
        SearchConfig searchConfig = new SearchConfig(RESULT_COUNT_DEFAULT, null, sort, null);
        List<Document> docs = catalogSearcher.fuzzySearchContents(text, searchConfig).getList();
        logger.debug("TEST fuzzySearchContents finish, searched doc count={}", docs.size());
        for (Document doc : docs) {
            logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
        }
        int docCount = docs.size();
        assertEquals(docCount, catalogSearcher.fuzzySearchContents(text.toLowerCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.fuzzySearchContents(text.toUpperCase(), searchConfig).getList().size());
        assertEquals(docCount, catalogSearcher.fuzzySearchContents(searchTestHelper.toVariableCase(text), searchConfig).getList().size());

        assertTrue(docCount > 0);
        assertEquals(expected, docs.get(0).get(SearchUtils.FIELD_NAME));
    }

    private int templateSearch(String text) throws Exception {
        logger.debug("TEST templateSearch start, text='{}'", text);
        List<Document> docs = catalogSearcher.templateSearchContents(text, SEARCH_CONFIG_DEFAULT).getList();
        logger.debug("TEST templateSearch finish, searched doc count={}", docs.size());
        for (Document doc : docs) {
            logger.debug("TEST doc: {}, {}", new Object[]{doc.get(SearchUtils.FIELD_NAME), doc.get(SearchUtils.FIELD_IDENTITY)});
        }
        int docCount = docs.size();
        assertEquals(docCount, catalogSearcher.templateSearchContents(text.toLowerCase(), SEARCH_CONFIG_DEFAULT).getList().size());
        assertEquals(docCount, catalogSearcher.templateSearchContents(text.toUpperCase(), SEARCH_CONFIG_DEFAULT).getList().size());
        assertEquals(docCount, catalogSearcher.templateSearchContents(searchTestHelper.toVariableCase(text), SEARCH_CONFIG_DEFAULT).getList().size());
        return docCount;
    }

    private void logDocumentList(List<Document> list) {
        for (Document doc : list) {
            logger.debug("TEST doc: {}: {}, {}, {}, {}, {}",
                    new Object[]{doc.get(SearchUtils.FIELD_IDENTITY),
                    doc.get(SearchUtils.FIELD_NAME),
                    doc.get(SearchUtils.FIELD_REGION),
                    doc.get(SearchUtils.FIELD_PRICE_MIN),
                    doc.get(SearchUtils.FIELD_PRICE_MAX),
                    doc.get(SearchUtils.FIELD_CONTENTS)});
        }
    }
}
