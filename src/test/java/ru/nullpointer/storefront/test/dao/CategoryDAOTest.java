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
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.UnitDAO;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Unit;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CategoryDAOTest {

    private Logger logger = LoggerFactory.getLogger(CategoryDAOTest.class);
    //
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private UnitDAO unitDAO;
    @Resource
    private OfferDAO offerDAO;
    //

    @Test
    public void testGetCategoryId() {
        CatalogItem section = catalogItemDAO.getChildrenList(null, false).get(0);
        CatalogItem categoryItem = catalogItemDAO.getChildrenList(section.getId(), false).get(0);

        assertNotNull(categoryItem);
        assertEquals(CatalogItem.Type.CATEGORY, categoryItem.getType());

        Category category = categoryDAO.getCategoryById(categoryItem.getId());
        assertNotNull(category);
    }

    @Test
    public void testGetCategoryMap() {
        List<CatalogItem> itemList = catalogItemDAO.getSubTree(null);

        Set<Integer> categoryIdSet = new HashSet<Integer>();
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryIdSet.add(item.getId());
            }
        }

        Map<Integer, Category> categoryMap = categoryDAO.getCategoryMap(categoryIdSet);

        assertNotNull(categoryMap);
        assertEquals(categoryMap.size(), categoryIdSet.size());

        for (Integer catId : categoryMap.keySet()) {
            assertTrue(categoryIdSet.contains(catId));

            Category category = categoryMap.get(catId);

            assertNotNull(category);
            assertEquals(catId, category.getId());
            assertNotNull(category.getUnitId());
        }
    }

    @Test
    @Rollback(true)
    public void testMethodGetAllCategories() {
        List<Category> allCategories = categoryDAO.getAllCategories();
        assertNotNull(allCategories);
        assertTrue(allCategories.size() > 0);
    }

    @Test
    @Rollback(true)
    public void testMethodAddCategory() {

        Category testCategory = null;

        logger.debug("TEST: null arguments");
        try {
            categoryDAO.addCategory(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }
        testCategory = new Category();
        testCategory.setId(null);
        try {
            categoryDAO.addCategory(testCategory);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }
        testCategory.setId(-1);
        testCategory.setUnitId(null);
        try {
            categoryDAO.addCategory(testCategory);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        // continue in CatalogServiceTest
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateCategoryInfo0() {
        Category testCategory = new Category();
        List<Category> allCategories = null;

        logger.debug("TEST: null arguments");
        try {
            categoryDAO.updateCategoryInfo(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }
        testCategory.setId(null);
        try {
            categoryDAO.updateCategoryInfo(testCategory);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        logger.debug("TEST: несуществующая категория");
        testCategory.setId(-1);
        try {
            categoryDAO.updateCategoryInfo(testCategory);
            //fail(); // TODO проверка на отсутствие?
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        logger.debug("TEST: невалидные атрибуты");
        testCategory.setUnitId(null);
        try {
            categoryDAO.updateCategoryInfo(testCategory);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }
        allCategories = categoryDAO.getAllCategories();
        testCategory = allCategories.get(0);
        testCategory.setUnitId(-1);
        try {
            categoryDAO.updateCategoryInfo(testCategory);
            fail();
        } catch (Exception ex) {
            logger.debug("TEST: Waited exception in testMethodUpdateCategoryInfo0");
        }
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateCategoryInfo1() {
        Category testCategory = new Category();
        List<Category> allCategories = null;
        List<Category> actualCategories = null;

        logger.debug("TEST: обновление категории");
        allCategories = categoryDAO.getAllCategories();
        testCategory = allCategories.get(0);
        List<Unit> allUnits = unitDAO.getAllUnits();
        Unit testUnit = null;
        for (Unit unit : allUnits) {
            if (!unit.getId().equals(testCategory.getUnitId())) {
                testUnit = unit;
                break;
            }
        }
        testCategory.setUnitId(testUnit.getId());
        //
        categoryDAO.updateCategoryInfo(testCategory);
        //
        actualCategories = categoryDAO.getAllCategories();
        Category actualCategory = actualCategories.get(0);
        assertEquals(testCategory.getId(), actualCategory.getId());
        assertEquals(testCategory.getUnitId(), actualCategory.getUnitId());
        assertEquals(testUnit.getId(), actualCategory.getUnitId());
    }

    @Test
    @Rollback(true)
    public void testMethodDeleteCategory0() {
        logger.debug("TEST: null arguments");
        try {
            categoryDAO.deleteCategory(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        Category testCategory = null;
        List<Category> allCategories = null;

        logger.debug("TEST: попытка удаления категории с товарными предложениями");

        allCategories = categoryDAO.getAllCategories();
        for (Category category : allCategories) {
            if (offerDAO.getCategoryOfferCount(category.getId()) > 0) {
                testCategory = category;
                break;
            }
        }
        try {
            categoryDAO.deleteCategory(testCategory.getId());
            fail();
        } catch (Exception ex) {
            logger.debug("TEST: Waited exception in testMethodDeleteCategory0");
        }
    }

    @Test
    @Rollback(true)
    public void testMethodDeleteCategory1() {
        logger.debug("TEST: null arguments");
        try {
            categoryDAO.deleteCategory(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //ex.printStackTrace();
        }

        Category testCategory = null;
        List<Category> allCategories = null;
        Category actualCategory = null;
        List<Category> actualCategories = null;

        logger.debug("TEST: удаление категории без товарных предложений");

        allCategories = categoryDAO.getAllCategories();
        for (Category category : allCategories) {
            if (offerDAO.getCategoryOfferCount(category.getId()) == 0) {
                testCategory = category;
                break;
            }
        }
        //
        categoryDAO.deleteCategory(testCategory.getId());
        //
        actualCategories = categoryDAO.getAllCategories();
        assertEquals(allCategories.size() - 1, actualCategories.size());
        actualCategory = categoryDAO.getCategoryById(testCategory.getId());
        assertNull(actualCategory);
    }
}
