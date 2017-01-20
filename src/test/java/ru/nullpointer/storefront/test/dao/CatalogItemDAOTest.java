package ru.nullpointer.storefront.test.dao;

import java.util.ArrayList;
import java.util.List;
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
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.test.AssertUtils;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CatalogItemDAOTest {

    private Logger logger = LoggerFactory.getLogger(CatalogItemDAOTest.class);
    //
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private BrandDAO brandDAO;
    @Resource
    private CompanyDAO companyDAO;

    @Test
    public void testRootSections() {
        List<CatalogItem> list = catalogItemDAO.getChildrenList(null, false);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        for (CatalogItem item : list) {
            assertNotNull(item);

            assertNotNull(item.getId());

            assertNotNull(item.getName());
            assertFalse(item.getName().trim().isEmpty());

            assertNotNull(item.getPath());
            assertEquals(2, item.getPath().length());

            assertEquals(CatalogItem.Type.SECTION, item.getType());
        }
    }

    @Test
    public void testSecondLevelItems() {
        CatalogItem section = catalogItemDAO.getChildrenList(null, false).get(0);

        String path = section.getPath();

        List<CatalogItem> list = catalogItemDAO.getChildrenList(section.getId(), false);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        logger.debug("--{}", section);

        for (CatalogItem item : list) {
            assertNotNull(item);

            assertNotNull(item.getId());

            assertNotNull(item.getName());
            assertFalse(item.getName().trim().isEmpty());

            assertNotNull(item.getPath());
            assertEquals(4, item.getPath().length());
            assertTrue(item.getPath().startsWith(path));

            assertNotNull(item.getType());

            logger.debug("----{}", item);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPorularSectionId() {
        catalogItemDAO.getPopularCategories(null);
    }

    @Test
    public void testPopularCategories() {
        CatalogItem section = catalogItemDAO.getChildrenList(null, false).get(0);

        String path = section.getPath();

        List<CatalogItem> list = catalogItemDAO.getPopularCategories(section.getId());

        assertNotNull(list);
        assertFalse(list.isEmpty());

        for (CatalogItem item : list) {
            assertNotNull(item);

            assertNotNull(item.getId());

            assertNotNull(item.getName());
            assertFalse(item.getName().trim().isEmpty());

            assertNotNull(item.getPath());
            assertTrue(item.getPath().startsWith(path));

            assertEquals(CatalogItem.Type.CATEGORY, item.getType());
        }
    }

    @Test
    public void testGetSectionItem() {
        CatalogItem s1 = catalogItemDAO.getChildrenList(null, false).get(0);
        assertEquals(CatalogItem.Type.SECTION, s1.getType());

        Integer id = s1.getId();

        CatalogItem s2 = catalogItemDAO.getSectionItemById(id);
        assertNotNull(s2);

        assertEquals(s1.getId(), s2.getId());
        assertEquals(s1.getName(), s2.getName());
        assertEquals(s1.getPath(), s2.getPath());
        assertEquals(s1.getType(), s2.getType());
    }

    @Test
    public void testInvalidSectionItem() {
        assertNull(catalogItemDAO.getSectionItemById(-100));
    }

    @Test
    public void testSecondLevelPath() {
        CatalogItem section = catalogItemDAO.getChildrenList(null, false).get(0);

        List<CatalogItem> pathElements = catalogItemDAO.getPath(section.getId());

        assertNotNull(pathElements);
        assertTrue(pathElements.size() == 1);

        List<CatalogItem> list = catalogItemDAO.getChildrenList(section.getId(), false);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        for (CatalogItem item : list) {
            pathElements = catalogItemDAO.getPath(item.getId());

            assertNotNull(pathElements);
            assertTrue(pathElements.size() == 2);

            assertEquals(pathElements.get(0).getId(), section.getId());
            assertEquals(pathElements.get(1).getId(), item.getId());
        }
    }

    @Test
    public void testInvalidPath() {
        List<CatalogItem> path = catalogItemDAO.getPath(-100);
        assertTrue(path.isEmpty());
    }

    @Test
    public void testMethodGetSubTree() {

        List<CatalogItem> list;

        // test: invalid sectionId
        list = catalogItemDAO.getSubTree(-1);
        assertNotNull(list);
        assertTrue(list.isEmpty());

        // test: get all items
        list = catalogItemDAO.getSubTree(null);
        assertNotNull(list);
        assertFalse(list.isEmpty());

        for (CatalogItem item : list) {
            assertNotNull(item);

            assertNotNull(item.getId());

            assertNotNull(item.getName());
            assertFalse(item.getName().trim().isEmpty());

            assertNotNull(item.getPath());
        }

        // validation sub tree (for all sections and categories)
        validationSubTree(null);
    }

    private void validationSubTree(CatalogItem rootItem) {
        // Обход иерархии каталога
        // Рекурсия
        // Избыточность: повторение catalogItemDAO.getSubTree
        // TODO проверка на целостность?

        List<CatalogItem> list = catalogItemDAO.getSubTree(rootItem == null ? null : rootItem.getId());

        for (CatalogItem item : list) {
            assertTrue(rootItem == null ? true : item.getPath().startsWith(rootItem.getPath()));
            validationSubTree(item);
        }
    }

    @Test
    @Rollback(true)
    public void testMethodAddItem() throws CatalogItemException {

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.addItem(null, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        CatalogItem testItemSection = new CatalogItem();
        testItemSection.setActive(false);
        testItemSection.setType(CatalogItem.Type.SECTION);
        // testItemSection.setName("Test SECTION 0");

        CatalogItem testItemCategory = new CatalogItem();
        testItemCategory.setActive(false);
        testItemCategory.setType(CatalogItem.Type.CATEGORY);
        // testItemCategory.setName("Test CATEGORY 0");

        logger.debug("TEST: несуществующий родительский раздел");
        try {
            catalogItemDAO.addItem(testItemSection, -1);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: пустой тип раздела(категории)");
        testItemSection.setType(null);
        testItemSection.setName("TEST");
        try {
            catalogItemDAO.addItem(testItemSection, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItemSection.setType(CatalogItem.Type.SECTION);

        logger.debug("TEST: пустое наименование раздела(категории)");
        testItemSection.setName(null);
        try {
            catalogItemDAO.addItem(testItemSection, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItemSection.setName("");
        try {
            catalogItemDAO.addItem(testItemSection, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> allItems = catalogItemDAO.getSubTree(null);
        List<CatalogItem> rootSections = null;
        List<CatalogItem> testItems = null;

        logger.debug("TEST: Нарушение субординации иерархии каталога - добавление категории(раздела) в категорию");
        // get CATEGORY
        CatalogItem categoryItem = null;
        for (CatalogItem item : allItems) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryItem = item;
                break;
            }
        }
        try {
            catalogItemDAO.addItem(testItemSection, categoryItem.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - добавление категории в корень каталога");
        try {
            catalogItemDAO.addItem(testItemCategory, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Добавление раздела в корень каталога");
        rootSections = catalogItemDAO.getChildrenList(null, false);
        List<List<CatalogItem>> rootSubTrees = new ArrayList<List<CatalogItem>>();
        for (CatalogItem item : rootSections) {
            rootSubTrees.add(catalogItemDAO.getSubTree(item.getId()));
        }
        testItemSection.setName("TEST LAST ROOT SECTION");
        testItemSection.setTheme("TEST THEME");
        //
        catalogItemDAO.addItem(testItemSection, null);
        //
        testItems = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : allItems) {
            logger.debug("TEST: old catalog item {}", item);
        }
        logger.debug("TEST: added item {}", testItemSection);
        for (CatalogItem item : testItems) {
            logger.debug("TEST: new catalog item {}", item);
        }
        assertEquals(allItems.size() + 1, testItems.size());
        testItems = catalogItemDAO.getChildrenList(null, false);
        assertEquals(rootSections.size() + 1, testItems.size());
        CatalogItem lastRootItem = testItems.get(testItems.size() - 1);
        assertEquals(lastRootItem.getPath(), String.format("%02d", testItems.size() - 1));
        AssertUtils.assertPropertiesEquals(testItemSection, lastRootItem, "name", "theme", "path");
        for (int i = 0; i < rootSections.size(); i++) {
            equalsCatalogItems(rootSections.get(i), testItems.get(i));
            List<CatalogItem> rootSubTree = rootSubTrees.get(i);
            List<CatalogItem> testSubTree = catalogItemDAO.getSubTree(testItems.get(i).getId());
            assertEquals(rootSubTree.size(), testSubTree.size());
            for (int j = 0; j < rootSubTree.size(); j++) {
                equalsCatalogItems(rootSubTree.get(j), testSubTree.get(j));
            }
        }

        logger.debug("TEST: Добавление категории в раздел каталога");
        allItems = catalogItemDAO.getSubTree(null);
        // get SECTION
        CatalogItem sectionItem = null;
        for (CatalogItem item : allItems) {
            if (item.getType() == CatalogItem.Type.SECTION) {
                sectionItem = item;
                break;
            }
        }
        List<CatalogItem> sectionItemChildrenOld = catalogItemDAO.getChildrenList(sectionItem.getId(), false);
        List<List<CatalogItem>> sectionItemChildrenSubTrees = new ArrayList<List<CatalogItem>>();
        for (CatalogItem item : sectionItemChildrenOld) {
            sectionItemChildrenSubTrees.add(catalogItemDAO.getSubTree(item.getId()));
        }
        testItemCategory.setType(CatalogItem.Type.CATEGORY);
        testItemCategory.setName("TEST CATEGORY");
        testItemCategory.setTheme("TEST THEME");
        //
        catalogItemDAO.addItem(testItemCategory, sectionItem.getId());
        //
        testItems = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : allItems) {
            logger.debug("TEST: old catalog item {}", item);
        }
        logger.debug("TEST: added item {}", testItemCategory);
        for (CatalogItem item : testItems) {
            logger.debug("TEST: new catalog item {}", item);
        }
        assertEquals(allItems.size() + 1, testItems.size());
        List<CatalogItem> sectionItemChildrenNew = catalogItemDAO.getChildrenList(sectionItem.getId(), false);
        assertEquals(sectionItemChildrenOld.size() + 1, sectionItemChildrenNew.size());
        CatalogItem lastChildItem = sectionItemChildrenNew.get(sectionItemChildrenNew.size() - 1);
        assertEquals(lastChildItem.getPath(), sectionItem.getPath() + String.format("%02d", sectionItemChildrenNew.size() - 1));

        AssertUtils.assertPropertiesEquals(testItemCategory, lastChildItem, "name", "theme", "path");

        for (int i = 0; i < sectionItemChildrenOld.size(); i++) {
            CatalogItem childItem = sectionItemChildrenOld.get(i);
            CatalogItem testChildItem = sectionItemChildrenNew.get(i);
            assertEquals(childItem.getId(), testChildItem.getId());
            assertEquals(childItem.getName(), testChildItem.getName());
            assertEquals(childItem.getType(), testChildItem.getType());
            assertEquals(childItem.getPath(), testChildItem.getPath());
            List<CatalogItem> childSubTree = sectionItemChildrenSubTrees.get(i);
            List<CatalogItem> testSubTree = catalogItemDAO.getSubTree(sectionItemChildrenNew.get(i).getId());
            assertEquals(childSubTree.size(), testSubTree.size());
            for (int j = 0; j < childSubTree.size(); j++) {
                CatalogItem item = childSubTree.get(j);
                CatalogItem testItem = testSubTree.get(j);

                AssertUtils.assertPropertiesEquals(item, testItem,
                        "id", "name", "theme", "path", "type");
            }
        }
        validationSubTree(null);
    }

    @Test
    @Rollback(true)
    public void testMethodDeleteItem() throws CatalogItemException {

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.deleteItem(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующий раздел или категория");
        try {
            catalogItemDAO.deleteItem(-1);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> testItems = null;

        List<CatalogItem> allItems = null;
        CatalogItem deleteItem = null;
        CatalogItem deleteParentItem = null;
        String deleteItemPath = null;

        deleteItemPath = "0300";
        logger.debug("TEST: Удаление раздела " + deleteItemPath);
        allItems = catalogItemDAO.getSubTree(null);
        deleteItem = null;
        for (CatalogItem item : allItems) {
            if (item.getPath().equals(deleteItemPath.substring(0, deleteItemPath.length() - 2))) {
                deleteParentItem = item;
            }
            if (item.getPath().equals(deleteItemPath)) {
                deleteItem = item;
                break;
            }
        }
        List<CatalogItem> childrenList = catalogItemDAO.getChildrenList(deleteParentItem.getId(), false);
        logger.debug("TEST: Удаляемый раздел {}", deleteItem);
        logger.debug("TEST: Родительский раздел удаляемого раздела {}", deleteParentItem);
        //
        catalogItemDAO.deleteItem(deleteItem.getId());
        //
        testItems = catalogItemDAO.getChildrenList(deleteParentItem.getId(), false);
        assertEquals(childrenList.size() - 1, testItems.size());
        for (int i = childrenList.size() - 1; i > 0; i--) {
            CatalogItem expectedItem = childrenList.get(i);
            CatalogItem actualItem = testItems.get(i - 1);
            assertEquals(expectedItem.getId(), actualItem.getId());
            assertEquals(expectedItem.getName(), actualItem.getName());
            assertEquals(expectedItem.getType(), actualItem.getType());
            String path = expectedItem.getPath();
            String expectedPath = deleteParentItem.getPath() + String.format("%02d", Integer.parseInt(path.substring(path.length() - 2, path.length()), 10) - 1);
            logger.debug("TEST: expectedPath = {} in {}", expectedPath, actualItem);
            assertEquals(expectedPath, actualItem.getPath());
        }

        // "03" -> "02"
        deleteRootItem("02");

        // TEST "02" ("03" -> "02") - multiple delete - SECTION only - если надо
        deleteRootItem("02");

        // TODO TEST - multiple delete - SECTION and CATEGORY - cascade delete -
        // если надо
        // deleteRootItem("01");

        //
        validationSubTree(null);
    }

    void deleteRootItem(String deleteItemPath) throws CatalogItemException {
        logger.debug("TEST: Удаление корневого раздела " + deleteItemPath);
        int deleteItemOrder = Integer.parseInt(deleteItemPath, 10);
        List<CatalogItem> rootSections = catalogItemDAO.getChildrenList(null, false);
        CatalogItem deleteItem = null;
        for (CatalogItem item : rootSections) {
            if (item.getPath().equals(deleteItemPath)) {
                deleteItem = item;
                break;
            }
        }
        // List<CatalogItem> childrenList =
        // catalogItemDAO.getChildrenList(null);
        logger.debug("TEST: Удаляемый раздел {}", deleteItem);
        logger.debug("TEST: Порядок удаляемого раздела {}", deleteItemOrder);

        List<CatalogItem> noChangedList = new ArrayList<CatalogItem>();
        List<List<CatalogItem>> noChangedListSubTrees = new ArrayList<List<CatalogItem>>();
        List<CatalogItem> changedList = new ArrayList<CatalogItem>();
        List<List<CatalogItem>> changedListSubTrees = new ArrayList<List<CatalogItem>>();
        for (int i = 0; i < rootSections.size(); i++) {
            if (i < deleteItemOrder) {
                noChangedList.add(rootSections.get(i));
                noChangedListSubTrees.add(catalogItemDAO.getSubTree(rootSections.get(i).getId()));
            } else if (i > deleteItemOrder) {
                changedList.add(rootSections.get(i));
                changedListSubTrees.add(catalogItemDAO.getSubTree(rootSections.get(i).getId()));
            }
        }
        //
        catalogItemDAO.deleteItem(deleteItem.getId());
        //
        List<CatalogItem> testItems = catalogItemDAO.getChildrenList(null, false);
        assertEquals(rootSections.size() - 1, testItems.size());
        for (int i = 0; i < noChangedList.size(); i++) {
            CatalogItem expectedItem = noChangedList.get(i);
            CatalogItem actualItem = testItems.get(i);
            assertEquals(expectedItem.getId(), actualItem.getId());
            assertEquals(expectedItem.getName(), actualItem.getName());
            assertEquals(expectedItem.getType(), actualItem.getType());
            assertEquals(expectedItem.getPath(), actualItem.getPath());
            List<CatalogItem> expectedItemSubTree = noChangedListSubTrees.get(i);
            List<CatalogItem> actualItemSubTree = catalogItemDAO.getSubTree(actualItem.getId());
            assertEquals(expectedItemSubTree.size(), actualItemSubTree.size());
            for (int j = 0; j < expectedItemSubTree.size(); j++) {
                expectedItem = expectedItemSubTree.get(j);
                actualItem = actualItemSubTree.get(j);
                assertEquals(expectedItem.getId(), actualItem.getId());
                assertEquals(expectedItem.getName(), actualItem.getName());
                assertEquals(expectedItem.getType(), actualItem.getType());
                assertEquals(expectedItem.getPath(), actualItem.getPath());
            }
        }
        for (int i = 0; i < changedList.size(); i++) {
            CatalogItem expectedItem = changedList.get(i);
            CatalogItem actualItem = testItems.get(deleteItemOrder + i);
            assertEquals(expectedItem.getId(), actualItem.getId());
            assertEquals(expectedItem.getName(), actualItem.getName());
            assertEquals(expectedItem.getType(), actualItem.getType());

            String expectedPath = String.format("%02d", deleteItemOrder + i);
            logger.debug("TEST: expectedPath = {} in {}", expectedPath, actualItem);
            assertEquals(expectedPath, actualItem.getPath());

            List<CatalogItem> expectedItemSubTree = changedListSubTrees.get(i);
            List<CatalogItem> actualItemSubTree = catalogItemDAO.getSubTree(actualItem.getId());
            assertEquals(expectedItemSubTree.size(), actualItemSubTree.size());
            for (int j = 0; j < expectedItemSubTree.size(); j++) {
                expectedItem = expectedItemSubTree.get(j);
                actualItem = actualItemSubTree.get(j);
                assertEquals(expectedItem.getId(), actualItem.getId());
                assertEquals(expectedItem.getName(), actualItem.getName());
                assertEquals(expectedItem.getType(), actualItem.getType());

                String testPath = expectedPath + expectedItem.getPath().substring(expectedPath.length());
                logger.debug("TEST: testPath = {} in {}", testPath, actualItem);
                assertEquals(testPath, actualItem.getPath());
            }
        }
    }

    @Test
    @Rollback(true)
    public void testMethodMoveItem() throws CatalogItemException {
        // catalogItemDAO.moveItem(null, null, null); if (true) return;

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.moveItem(null, null, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> allItems = catalogItemDAO.getSubTree(null);
        List<CatalogItem> rootSections = catalogItemDAO.getChildrenList(null, false);
        List<CatalogItem> testItems = null;

        CatalogItem rootFirst = rootSections.get(0);
        CatalogItem rootLast = rootSections.get(rootSections.size() - 1);

        logger.debug("TEST: несуществующие разделы или категории");
        try {
            catalogItemDAO.moveItem(-1, null, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), -1, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), null, -1);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - item = after item");
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), rootFirst.getId(), null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - item = section item");
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), null, rootFirst.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - after item = section item");
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), rootLast.getId(), rootLast.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - перемещение раздела(категории) во внутрь себя");
        testItems = catalogItemDAO.getSubTree(rootFirst.getId());
        CatalogItem innerSection = null;
        for (CatalogItem item : testItems) {
            if (item.getType() == CatalogItem.Type.SECTION) {
                innerSection = item;
                break;
            }
        }
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), testItems.get(0).getId(), null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), null, innerSection.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        // get CATEGORY
        CatalogItem categoryItem = null;
        for (CatalogItem item : allItems) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryItem = item;
                break;
            }
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - перемещение раздела(категории) в категорию");
        try {
            catalogItemDAO.moveItem(rootFirst.getId(), null, categoryItem.getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: Нарушение субординации иерархии каталога - перемещение категории в корень каталога");
        try {
            catalogItemDAO.moveItem(categoryItem.getId(), null, null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        try {
            catalogItemDAO.moveItem(categoryItem.getId(), rootFirst.getId(), null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        //
        List<CatalogItem> testRootSections = null;
        List<CatalogItem> newCatalogTree = null;

        logger.debug("TEST: Отсутствие фактических изменений (1)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        //
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), null, null);
        //
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Отсутствие фактических изменений (2)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        //
        catalogItemDAO.moveItem(testRootSections.get(0).getId(), testRootSections.get(1).getId(), null);
        //
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Отсутствие фактических изменений (3)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        testItems = catalogItemDAO.getChildrenList(testRootSections.get(0).getId(), false);
        //
        catalogItemDAO.moveItem(testItems.get(testItems.size() - 1).getId(), null, testRootSections.get(0).getId());
        //
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение между уровнями - перенос первого раздела верхнего уровня в последний раздел верхнего уровня");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        for (CatalogItem item : allItems) {
            logger.debug("TEST: old catalog item {}", item);
        }

        CatalogItem expectItem = testRootSections.get(0);
        CatalogItem expectParentItem = testRootSections.get(testRootSections.size() - 1);
        assertNotNull(expectItem);
        assertNotNull(expectParentItem);
        assertTrue(expectItem.getType() == CatalogItem.Type.SECTION);
        assertTrue(expectParentItem.getType() == CatalogItem.Type.SECTION);
        assertFalse(expectItem.equals(expectParentItem));
        CatalogItem secondRootItem = testRootSections.get(1);
        assertNotNull(secondRootItem);

        List<CatalogItem> oldParentItemChildrenList = catalogItemDAO.getChildrenList(expectParentItem.getId(), false);
        int expectRootSectionsSize = testRootSections.size() - 1;
        int expectParentItemChildrenCount = oldParentItemChildrenList.size() + 1;
        expectParentItem.setPath(String.format("%02d", expectRootSectionsSize - 1));
        expectItem.setPath(expectParentItem.getPath() + String.format("%02d", expectParentItemChildrenCount - 1));

        logger.debug("TEST: expected item path {} -> {}", expectItem.getPath(), expectItem);

        List<CatalogItem> expectedRootSections = testRootSections.subList(1, testRootSections.size());
        for (int i = 0; i < expectedRootSections.size(); i++) {
            expectedRootSections.get(i).setPath(String.format("%02d", i));
            logger.debug("TEST: expected root item path {} -> {}", expectedRootSections.get(i).getPath(), expectedRootSections.get(i));
        }
        //
        catalogItemDAO.moveItem(expectItem.getId(), null, expectParentItem.getId());
        //
        CatalogItem actualItem = catalogItemDAO.getSectionItemById(expectItem.getId());
        CatalogItem actualParentItem = catalogItemDAO.getSectionItemById(expectParentItem.getId());

        assertEquals(expectItem.getPath(), actualItem.getPath());
        assertEquals(expectParentItem.getPath(), actualParentItem.getPath());

        List<CatalogItem> actualParentItemChildrenList = catalogItemDAO.getChildrenList(actualParentItem.getId(), false);
        assertEquals(expectParentItemChildrenCount, actualParentItemChildrenList.size());

        List<CatalogItem> actualRootSections = catalogItemDAO.getChildrenList(null, false);
        assertEquals(expectRootSectionsSize, actualRootSections.size());
        for (int i = 0; i < expectedRootSections.size(); i++) {
            assertEquals(expectedRootSections.get(i).getPath(), actualRootSections.get(i).getPath());

            List<CatalogItem> actualRootSectionSubTree = catalogItemDAO.getSubTree(actualRootSections.get(i).getId());
            for (CatalogItem item : actualRootSectionSubTree) {
                assertTrue(item.getPath().startsWith(expectedRootSections.get(i).getPath()));
            }
        }

        newCatalogTree = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : newCatalogTree) {
            logger.debug("TEST: new catalog item {}", item);
        }
        assertEquals(allItems.size(), newCatalogTree.size());

        logger.debug("TEST: ... и обратно");
        catalogItemDAO.moveItem(expectItem.getId(), secondRootItem.getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : newCatalogTree) {
            logger.debug("TEST: new catalog item {}", item);
        }
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение в пределах одного уровня - перенос первого раздела верхнего уровня в конец и обратно");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(0).getId(), null, null);
        catalogItemDAO.moveItem(testRootSections.get(0).getId(), testRootSections.get(1).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение в пределах одного уровня - перенос последнего раздела верхнего уровня в начало и обратно");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), testRootSections.get(0).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), null, null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение в пределах одного уровня - перенос раздела из \"середины\" верхнего уровня в конец и обратно");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get((testRootSections.size() - 1) / 2).getId(), null, null);
        catalogItemDAO.moveItem(testRootSections.get((testRootSections.size() - 1) / 2).getId(), testRootSections.get((testRootSections.size() - 1) / 2 + 1).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("Перемещение в пределах одного уровня - перенос раздела из \"середины\" верхнего уровня в начало и обратно");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get((testRootSections.size() - 1) / 2).getId(), testRootSections.get(0).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get((testRootSections.size() - 1) / 2).getId(), testRootSections.get((testRootSections.size() - 1) / 2 + 1).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение на один порядок - перемена мест первого и второго разделов верхнего уровня и обратно (способ 1)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(1).getId(), testRootSections.get(0).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get(0).getId(), testRootSections.get(1).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение на один порядок - перемена мест первого и второго разделов верхнего уровня и обратно (способ 2)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(1).getId(), testRootSections.get(0).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get(1).getId(), testRootSections.get(2).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение на один порядок - перемена мест последнего и предпоследнего разделов верхнего уровня и обратно (способ 1)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), testRootSections.get(testRootSections.size() - 2).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 2).getId(), testRootSections.get(testRootSections.size() - 1).getId(), null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Перемещение на один порядок - перемена мест последнего и предпоследнего разделов верхнего уровня и обратно (способ 2)");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), testRootSections.get(testRootSections.size() - 2).getId(), null);
        catalogItemDAO.moveItem(testRootSections.get(testRootSections.size() - 1).getId(), null, null);
        newCatalogTree = catalogItemDAO.getSubTree(null);
        assertEquals(allItems.size(), newCatalogTree.size());
        for (int i = 0; i < allItems.size(); i++) {
            logger.debug("TEST: actual item = {}", newCatalogTree.get(i));
            assertEquals(allItems.get(i).getPath(), newCatalogTree.get(i).getPath());
        }

        logger.debug("TEST: Указаны и after item, и section item");
        testRootSections = catalogItemDAO.getChildrenList(null, false);
        try {
            catalogItemDAO.moveItem(testRootSections.get(0).getId(), testRootSections.get(testRootSections.size() - 1).getId(), testRootSections.get(testRootSections.size() - 2).getId());
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        // assertTrue(false);
        //
        validationSubTree(null);
    }

    @Test
    @Rollback(true)
    public void testCatalogItemStructureEdit() throws CatalogItemException {
        // TODO TEST
        // testMethodAddItem();
        // testMethodDeleteItem();
        // testMethodMoveItem();
    }

    @Test
    public void testMethodGetItemLevel() {
        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getItemLevel(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        CatalogItem testItem = new CatalogItem();

        logger.debug("TEST: null, empty path");
        testItem.setPath(null);
        try {
            catalogItemDAO.getItemLevel(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItem.setPath("");
        try {
            catalogItemDAO.getItemLevel(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: invalid level(path)");
        testItem.setPath("0");
        try {
            catalogItemDAO.getItemLevel(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItem.setPath("123456789");
        try {
            catalogItemDAO.getItemLevel(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: different path");
        testItem.setPath("00");
        assertEquals(1, catalogItemDAO.getItemLevel(testItem));
        testItem.setPath("99");
        assertEquals(1, catalogItemDAO.getItemLevel(testItem));
        testItem.setPath("0000");
        assertEquals(2, catalogItemDAO.getItemLevel(testItem));
        testItem.setPath("9999");
        assertEquals(2, catalogItemDAO.getItemLevel(testItem));
        testItem.setPath("00000000000000000000");
        assertEquals(10, catalogItemDAO.getItemLevel(testItem));
        testItem.setPath("99999999999999999999");
        assertEquals(10, catalogItemDAO.getItemLevel(testItem));
    }

    @Test
    @Rollback(true)
    public void testMethodGetItemById() {

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getItemById(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> allItems = null;
        CatalogItem testItem = null;

        logger.debug("TEST: несуществующий раздел или категория");
        testItem = catalogItemDAO.getItemById(-1);
        assertNull(testItem);

        logger.debug("TEST: произвольный раздел или категория");
        allItems = catalogItemDAO.getSubTree(null);
        CatalogItem lastItem = allItems.get(allItems.size() - 1);
        testItem = catalogItemDAO.getItemById(lastItem.getId());
        assertEquals(lastItem.getId(), testItem.getId());
        assertEquals(lastItem.getName(), testItem.getName());
        assertEquals(lastItem.getType(), testItem.getType());
        assertEquals(lastItem.getPath(), testItem.getPath());
    }

    @Test
    @Rollback(true)
    public void testMethodGetCategoryItemById() {

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getCategoryItemById(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> allItems = null;
        CatalogItem testItem = null;

        logger.debug("TEST: несуществующая категория");
        testItem = catalogItemDAO.getCategoryItemById(-1);
        assertNull(testItem);

        logger.debug("TEST: произвольная категория");
        allItems = catalogItemDAO.getSubTree(null);
        // get CATEGORY
        CatalogItem categoryItem = null;
        for (CatalogItem item : allItems) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryItem = item;
                break;
            }
        }
        testItem = catalogItemDAO.getCategoryItemById(categoryItem.getId());
        assertEquals(categoryItem.getId(), testItem.getId());
        assertEquals(categoryItem.getName(), testItem.getName());
        assertEquals(categoryItem.getType(), testItem.getType());
        assertEquals(categoryItem.getPath(), testItem.getPath());
    }

    @Test
    @Rollback(true)
    public void testMethodGetParent() {

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getParent(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующий раздел или категория");
        try {
            catalogItemDAO.getParent(-1);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> allItems = null;
        List<CatalogItem> rootSections = null;
        List<CatalogItem> testItems = null;
        CatalogItem testItem = null;
        CatalogItem testParentItem = null;
        CatalogItem actualParentItem = null;

        rootSections = catalogItemDAO.getChildrenList(null, false);

        logger.debug("TEST: раздел или категория первого уровня");
        testItem = rootSections.get(0);
        actualParentItem = catalogItemDAO.getParent(testItem.getId());
        assertNull(actualParentItem);

        logger.debug("TEST: раздел или категория второго уровня");
        testParentItem = rootSections.get(0);
        testItems = catalogItemDAO.getChildrenList(testParentItem.getId(), false);
        testItem = testItems.get(0);
        actualParentItem = catalogItemDAO.getParent(testItem.getId());
        equalsCatalogItems(testParentItem, actualParentItem);
        assertEquals("00", actualParentItem.getPath());

        logger.debug("TEST: предпоследний раздел или категория");
        allItems = catalogItemDAO.getSubTree(null);
        testItem = allItems.get(allItems.size() - 2);
        String path = testItem.getPath();
        String parentPath = path.substring(0, path.length() - 2);
        testParentItem = null;
        for (CatalogItem item : allItems) {
            if (item.getPath().equals(parentPath)) {
                testParentItem = item;
                break;
            }
        }
        actualParentItem = catalogItemDAO.getParent(testItem.getId());
        equalsCatalogItems(testParentItem, actualParentItem);
        assertEquals(parentPath, actualParentItem.getPath());
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateItemInfo() {
        CatalogItem testItem = new CatalogItem();

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.updateItemInfo(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItem.setId(null);
        try {
            catalogItemDAO.updateItemInfo(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующий раздел или категория");
        testItem.setId(-1);
        testItem.setName("UPDATED_NAME");
        try {
            catalogItemDAO.updateItemInfo(testItem);
            // fail(); // TODO проверка на отсутствие?
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: все элементы каталога");
        List<CatalogItem> catalogTree = catalogItemDAO.getSubTree(null);
        for (int i = 0; i < catalogTree.size(); i++) {
            test_updateItemInfo(catalogTree.get(i));
        }
    }

    @Test
    public void testGetActiveCatalogItemCount() {
        int count = catalogItemDAO.getActiveCatalogItemCount();

        int expected = 0;
        List<CatalogItem> tree = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : tree) {
            if (item.getActive()) {
                expected += 1;
            }
        }

        assertEquals(expected, count);
    }

    @Test
    public void testGetBrandActiveCategoryList() {
        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getBrandActiveCategoryList(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<Brand> brandList = brandDAO.getBrandList();

        assertFalse(brandList.isEmpty());

        boolean exist = false;

        for (Brand brand : brandList) {
            List<CatalogItem> brandCategories = catalogItemDAO.getBrandActiveCategoryList(brand.getId());

            if (!brandCategories.isEmpty()) {
                exist = true;

                for (CatalogItem item : brandCategories) {
                    assertTrue(item.getActive());
                    assertEquals(CatalogItem.Type.CATEGORY, item.getType());
                }
            }
        }

        assertTrue(exist);
    }

    @Test
    public void testGetCompanyActiveCategoryList() {
        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.getCompanyActiveCategoryList(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<Company> companyList = companyDAO.getCompanyList(null, new PageConfig(1, 100));

        assertFalse(companyList.isEmpty());

        boolean exist = false;

        for (Company company : companyList) {
            List<CatalogItem> companyCategories = catalogItemDAO.getCompanyActiveCategoryList(company.getId());

            if (!companyCategories.isEmpty()) {
                exist = true;

                for (CatalogItem item : companyCategories) {
                    assertTrue(item.getActive());
                    assertEquals(CatalogItem.Type.CATEGORY, item.getType());
                }
            }
        }

        assertTrue(exist);
    }

    private void test_updateItemInfo(CatalogItem treeItem) {
        CatalogItem testItem = new CatalogItem();

        testItem.setId(treeItem.getId());
        testItem.setPath("unusable path");
        testItem.setType(treeItem.getType().equals(CatalogItem.Type.CATEGORY) ? CatalogItem.Type.SECTION : CatalogItem.Type.CATEGORY);
        testItem.setName("UPD " + treeItem.getName());
        testItem.setTheme("new theme");

        catalogItemDAO.updateItemInfo(testItem);
        //
        CatalogItem actualItem = catalogItemDAO.getItemById(treeItem.getId());
        // Проверить изменения
        AssertUtils.assertPropertiesEquals(testItem, actualItem, "name", "theme");
        // Проверить поля которые не должны меняться
        AssertUtils.assertPropertiesEquals(treeItem, actualItem, "id", "path", "type", "active");
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateItemActive() {
        CatalogItem testItem = new CatalogItem();

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.updateItemActive(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItem.setId(null);
        try {
            catalogItemDAO.updateItemActive(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующий раздел или категория");
        testItem.setId(-1);
        testItem.setActive(Boolean.FALSE);
        try {
            catalogItemDAO.updateItemActive(testItem);
            // fail(); // TODO проверка на отсутствие?
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: все элементы каталога");
        List<CatalogItem> catalogTree = catalogItemDAO.getSubTree(null);
        for (int i = 0; i < catalogTree.size(); i++) {
            testUpdateItemActive(catalogTree.get(i), i);
        }
    }

    private void testUpdateItemActive(CatalogItem treeItem, int treeIndex) {
        CatalogItem testItem = new CatalogItem();

        testItem.setId(treeItem.getId());
        testItem.setPath("unusable path");
        testItem.setType(treeItem.getType().equals(CatalogItem.Type.CATEGORY) ? CatalogItem.Type.SECTION : CatalogItem.Type.CATEGORY);

        String testNamePref = "UPDATED_NAME ";
        String testName = testNamePref.concat(treeItem.getName());
        testItem.setName(testName);
        Boolean testActive = Boolean.valueOf(!treeItem.getActive().booleanValue());
        testItem.setActive(testActive);
        //
        catalogItemDAO.updateItemActive(testItem);
        //
        CatalogItem actualItem = catalogItemDAO.getSubTree(null).get(treeIndex);

        assertEquals(treeItem.getId(), actualItem.getId());
        assertEquals(testItem.getId(), actualItem.getId());

        assertEquals(treeItem.getPath(), actualItem.getPath());
        assertEquals(treeItem.getType(), actualItem.getType());
        assertEquals(treeItem.getName(), actualItem.getName());

        assertEquals(testActive, testItem.getActive());
        assertEquals(testActive, actualItem.getActive());
        assertEquals(Boolean.valueOf(!treeItem.getActive().booleanValue()), actualItem.getActive());
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateSectionTreeActive() {
        CatalogItem testItem = new CatalogItem();

        logger.debug("TEST: null arguments");
        try {
            catalogItemDAO.updateSectionTreeActive(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testItem.setId(null);
        try {
            catalogItemDAO.updateSectionTreeActive(testItem);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующий раздел или категория");
        testItem.setId(-1);
        testItem.setActive(Boolean.FALSE);
        try {
            catalogItemDAO.updateSectionTreeActive(testItem);
            // fail(); // TODO проверка на отсутствие?
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<CatalogItem> catalogTree = null;
        List<CatalogItem> actualTree = null;
        CatalogItem actualItem = null;

        logger.debug("TEST: категория каталога");
        catalogTree = catalogItemDAO.getSubTree(null);
        // get CATEGORY
        CatalogItem categoryItem = null;
        for (CatalogItem item : catalogTree) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryItem = item;
                break;
            }
        }
        testItem = new CatalogItem();
        testItem.setId(categoryItem.getId());
        testItem.setName(categoryItem.getName());
        testItem.setType(categoryItem.getType());
        testItem.setPath(categoryItem.getPath());
        testItem.setActive(Boolean.valueOf(!categoryItem.getActive().booleanValue()));
        //
        catalogItemDAO.updateSectionTreeActive(testItem);
        //
        actualTree = catalogItemDAO.getSubTree(null);
        assertEquals(catalogTree.size(), actualTree.size());
        for (int i = 0; i < catalogTree.size(); i++) {
            equalsCatalogItems(catalogTree.get(i), actualTree.get(i));
        }

        logger.debug("TEST: последний одиночный раздел каталога");
        catalogTree = catalogItemDAO.getSubTree(null);
        testItem = catalogTree.get(catalogTree.size() - 1);
        testItem.setActive(Boolean.valueOf(!testItem.getActive().booleanValue()));
        //
        catalogItemDAO.updateSectionTreeActive(testItem);
        //
        actualTree = catalogItemDAO.getSubTree(null);
        actualItem = actualTree.get(catalogTree.size() - 1);

        assertEquals(catalogTree.size(), actualTree.size());
        for (int i = 0; i < catalogTree.size() - 1; i++) {
            equalsCatalogItems(catalogTree.get(i), actualTree.get(i));
        }
        equalsCatalogItems(testItem, actualItem);

        logger.debug("TEST: первый раздел верхнего уровня");
        catalogTree = catalogItemDAO.getSubTree(null);
        testItem = catalogTree.get(0);
        testItem.setActive(Boolean.valueOf(!testItem.getActive().booleanValue()));
        //
        catalogItemDAO.updateSectionTreeActive(testItem);
        //
        actualTree = catalogItemDAO.getSubTree(null);
        List<CatalogItem> actualSubTree = catalogItemDAO.getSubTree(testItem.getId());

        assertEquals(catalogTree.size(), actualTree.size());
        for (int i = 1; i < catalogTree.size(); i++) {
            boolean subItem = false;
            for (CatalogItem item : actualSubTree) {
                if (item.getId().equals(actualTree.get(i).getId())) {
                    subItem = true;
                    break;
                }
            }
            if (subItem) {
                CatalogItem expectItem = catalogTree.get(i);
                actualItem = actualTree.get(i);
                assertEquals(expectItem.getId(), actualItem.getId());
                assertEquals(expectItem.getName(), actualItem.getName());
                assertEquals(expectItem.getPath(), actualItem.getPath());
                assertEquals(expectItem.getType(), actualItem.getType());
                assertEquals(testItem.getActive(), actualItem.getActive());
            } else {
                equalsCatalogItems(catalogTree.get(i), actualTree.get(i));
            }
        }
        equalsCatalogItems(testItem, actualTree.get(0));
    }

    @Test
    public void test_getChildrenList_onlyActive() {
        List<CatalogItem> itemList = catalogItemDAO.getChildrenList(null, true);
        assertNotNull(itemList);
        assertFalse(itemList.isEmpty());
        for (CatalogItem item : itemList) {
            assertTrue(item.getActive());
        }
    }

    private void equalsCatalogItems(CatalogItem expectItem, CatalogItem actualItem) {
        assertEquals(expectItem.getId(), actualItem.getId());
        assertEquals(expectItem.getName(), actualItem.getName());
        assertEquals(expectItem.getPath(), actualItem.getPath());
        assertEquals(expectItem.getType(), actualItem.getType());
        assertEquals(expectItem.getActive(), actualItem.getActive());
    }
}
