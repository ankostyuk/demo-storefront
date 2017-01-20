package ru.nullpointer.storefront.test.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.test.service.support.ServiceTestHelper;
import static org.junit.Assert.*;

/**
 * 
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CatalogServiceTest {

    private Logger logger = LoggerFactory.getLogger(CatalogServiceTest.class);
    //

    @Resource
    private ServiceTestHelper serviceTestHelper;

    @Resource
    private CatalogService catalogService;

    @Test
    @Rollback(true)
    public void testMethodAddCategory() {
        serviceTestHelper.authenticateAsManagerCatalog();

        CatalogItem testItem = null;
        Category testCategory = null;

        List<CatalogItem> expectedItemList = null;
        List<CatalogItem> actualItemList = null;

        List<Category> expectedCategoryList = null;
        List<Category> actualCategoryList = null;

        logger.debug("TEST: null arguments");
        try {
            catalogService.addCategory(null, new Category(), 1);
            fail();
        } catch (IllegalArgumentException ex) {
        } catch (CatalogItemException ex) {
        }
        try {
            catalogService.addCategory(new CatalogItem(), null, 1);
            fail();
        } catch (IllegalArgumentException ex) {
        } catch (CatalogItemException ex) {
        }
        try {
            catalogService.addCategory(new CatalogItem(), new Category(), null);
            fail();
        } catch (IllegalArgumentException ex) {
        } catch (CatalogItemException ex) {
        }

        logger.debug("TEST: Попытка добавления не категории");
        testItem = new CatalogItem();
        testItem.setName("SECTION");
        testItem.setType(CatalogItem.Type.SECTION);
        try {
            catalogService.addCategory(testItem, new Category(), 1);
            fail();
        } catch (IllegalArgumentException ex) {
        } catch (CatalogItemException ex) {
        }

        if (true)
            return; // TODO service transaction <-> test transaction

        logger.debug("TEST: Добавление категории в первый раздел каталога с неверными атрибутами");
        testItem = new CatalogItem();
        testItem.setName("TEST CATEGORY");
        testItem.setType(CatalogItem.Type.CATEGORY);
        testItem.setActive(Boolean.FALSE);
        testCategory = new Category();
        testCategory.setUnitId(null);
        CatalogItem sectionItem = catalogService.getChildrenList(null).get(0);

        expectedItemList = catalogService.getSubTree(null);
        expectedCategoryList = catalogService.getAllCategories();
        try {
            catalogService.addCategory(testItem, testCategory, sectionItem.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (CatalogItemException ex) {
            ex.printStackTrace();
        }
        actualItemList = catalogService.getSubTree(null);
        actualCategoryList = catalogService.getAllCategories();
        equalsCatalogItemLists(expectedItemList, actualItemList);
        equalsCategoryLists(expectedCategoryList, actualCategoryList);
        integrityCategoryCatalogItem();

    }

    @Test
    public void testMethodGetMergedPathList() {
        List<CatalogItem> tree = catalogService.getSubTree(null);

        for (int i = 0; i < 2; i++) {
            Set<Integer> treeIdSet = new HashSet<Integer>();
            for (CatalogItem item : tree) {
                treeIdSet.add(item.getId());
            }
            assertFalse(treeIdSet.isEmpty());

            List<List<CatalogItem>> list = catalogService.getMergedPathList(treeIdSet);
            assertEquals(14, list.size());

            tree.clear();
            for (List<CatalogItem> path : list) {
                for (CatalogItem item : path) {
                    tree.add(item);
                }
            }
        }
    }

    private void equalsCatalogItems(CatalogItem expectItem, CatalogItem actualItem) {
        assertEquals(expectItem.getId(), actualItem.getId());
        assertEquals(expectItem.getName(), actualItem.getName());
        assertEquals(expectItem.getPath(), actualItem.getPath());
        assertEquals(expectItem.getType(), actualItem.getType());
        assertEquals(expectItem.getActive(), actualItem.getActive());
    }

    private void equalsCategories(Category expectCategory, Category actualCategory) {
        assertEquals(expectCategory.getId(), actualCategory.getId());
        assertEquals(expectCategory.getUnitId(), actualCategory.getUnitId());
    }

    private void equalsCatalogItemLists(List<CatalogItem> expectedList, List<CatalogItem> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            equalsCatalogItems(expectedList.get(i), actualList.get(i));
        }
    }

    private void equalsCategoryLists(List<Category> expectedList, List<Category> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            equalsCategories(expectedList.get(i), actualList.get(i));
        }
    }

    private void integrityCategoryCatalogItem() {
        List<Category> allCategories = catalogService.getAllCategories();
        List<CatalogItem> allCategoryItems = catalogService.getSubCategories(null);
        assertEquals(allCategories.size(), allCategoryItems.size());
        for (Category category : allCategories) {
            CatalogItem categoryItem = null;
            for (CatalogItem item : allCategoryItems) {
                if (category.getId().equals(item.getId())) {
                    categoryItem = item;
                    break;
                }
            }
            assertNotNull(categoryItem);
        }
    }
}
