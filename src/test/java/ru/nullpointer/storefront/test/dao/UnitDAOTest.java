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
public class UnitDAOTest {

    private Logger logger = LoggerFactory.getLogger(UnitDAOTest.class);
    //
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private UnitDAO unitDAO;

    @Test
    public void testGetCategoryByCatalogItemId() {
        List<CatalogItem> itemList = catalogItemDAO.getSubTree(null);

        Set<Integer> unitIdSet = new HashSet<Integer>();
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Category cat = categoryDAO.getCategoryById(item.getId());
                unitIdSet.add(cat.getUnitId());
            }
        }

        assertTrue(unitIdSet.size() > 0);
        Map<Integer, Unit> unitMap = unitDAO.getUnitMap(unitIdSet);
        assertNotNull(unitMap);
        assertEquals(unitIdSet.size(), unitMap.size());

        for (Integer unitId : unitMap.keySet()) {
            Unit unit = unitMap.get(unitId);

            assertNotNull(unit);
            assertEquals(unitId, unit.getId());

            assertNotNull(unit.getName());
            assertNotNull(unit.getAbbreviation());
        }
    }

    @Test
    @Rollback(true)
    public void testMethodGetAllUnits() {
        List<Unit> allUnits = unitDAO.getAllUnits();
        assertNotNull(allUnits);
        assertTrue(allUnits.size() > 0);
    }

    @Test
    @Rollback(true)
    public void testMethodGetUnitById() {
        Unit actualUnit = null;

        logger.debug("TEST: null arguments");
        try {
            actualUnit = unitDAO.getUnitById(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: несуществующая единица измерения категорий");
        actualUnit = unitDAO.getUnitById(-1);
        assertNull(actualUnit);

        logger.debug("TEST: получение единицы измерения категорий");
        List<Unit> allUnits = unitDAO.getAllUnits();
        Unit testUnit = allUnits.get(0);
        //
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        //
        equalsUnits(testUnit, actualUnit);
    }

    @Test
    @Rollback(true)
    public void testMethodAddUnit() {
        Unit testUnit = null;

        logger.debug("TEST: null arguments");
        try {
            unitDAO.addUnit(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        testUnit = new Unit();
        testUnit.setName(null);
        try {
            unitDAO.addUnit(testUnit);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        List<Unit> allUnits = null;
        List<Unit> actualUnits = null;
        Unit actualUnit = null;

        logger.debug("TEST: добавление единицы измерения категорий без сокращенного наименования");
        allUnits = unitDAO.getAllUnits();
        testUnit = new Unit();
        testUnit.setName("TEST_UNIT");
        testUnit.setAbbreviation(null);
        //
        unitDAO.addUnit(testUnit);
        //
        actualUnits = unitDAO.getAllUnits();
        assertEquals(allUnits.size() + 1, actualUnits.size());
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        equalsUnits(testUnit, actualUnit);

        logger.debug("TEST: добавление единицы измерения категорий с сокращенным наименованием");
        allUnits = unitDAO.getAllUnits();
        testUnit = new Unit();
        testUnit.setName("TEST_UNIT");
        testUnit.setAbbreviation("TEST_ABBR");
        //
        unitDAO.addUnit(testUnit);
        //
        actualUnits = unitDAO.getAllUnits();
        assertEquals(allUnits.size() + 1, actualUnits.size());
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        equalsUnits(testUnit, actualUnit);
    }

    @Test
    @Rollback(true)
    public void testMethodUpdateUnitInfo() {
        Unit testUnit = null;
        List<Unit> allUnits = null;
        List<Unit> actualUnits = null;
        Unit actualUnit = null;

        logger.debug("TEST: null arguments");
        try {
            unitDAO.updateUnitInfo(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        allUnits = unitDAO.getAllUnits();
        testUnit = allUnits.get(0);
        testUnit.setId(null);
        try {
            unitDAO.updateUnitInfo(testUnit);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }
        allUnits = unitDAO.getAllUnits();
        testUnit = allUnits.get(0);
        testUnit.setName(null);
        try {
            unitDAO.updateUnitInfo(testUnit);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        logger.debug("TEST: обновление единицы измерения категорий без сокращенного наименования");
        allUnits = unitDAO.getAllUnits();
        testUnit = allUnits.get(0);
        testUnit.setName("TEST_UNIT");
        testUnit.setAbbreviation(null);
        //
        unitDAO.updateUnitInfo(testUnit);
        //
        actualUnits = unitDAO.getAllUnits();
        assertEquals(allUnits.size(), actualUnits.size());
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        equalsUnits(testUnit, actualUnit);

        logger.debug("TEST: обновление единицы измерения категорий с сокращенным наименованием");
        allUnits = unitDAO.getAllUnits();
        testUnit = allUnits.get(0);
        testUnit.setName("TEST_UNIT");
        testUnit.setAbbreviation("TEST_ABBR");
        //
        unitDAO.updateUnitInfo(testUnit);
        //
        actualUnits = unitDAO.getAllUnits();
        assertEquals(allUnits.size(), actualUnits.size());
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        equalsUnits(testUnit, actualUnit);
    }

    @Test
    @Rollback(true)
    public void testMethodDeleteUnit0() {
        logger.debug("TEST: null arguments");
        try {
            unitDAO.deleteUnit(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        Unit testUnit = null;

        logger.debug("TEST: попытка удаления единицы измерения категорий, связанной с категорией");
        testUnit = unitDAO.getUnitById(categoryDAO.getAllCategories().get(0).getUnitId());
        try {
            unitDAO.deleteUnit(testUnit.getId());
            fail();
        } catch (Exception ex) {
            logger.debug("TEST: Waited exception in testMethodDeleteUnit0");
        }
    }

    @Test
    @Rollback(true)
    public void testMethodDeleteUnit1() {
        logger.debug("TEST: null arguments");
        try {
            unitDAO.deleteUnit(null);
            fail();
        } catch (IllegalArgumentException ex) {
            // ex.printStackTrace();
        }

        Unit testUnit = null;
        List<Unit> allUnits = null;
        List<Unit> actualUnits = null;
        Unit actualUnit = null;

        logger.debug("TEST: удаление единицы измерения категорий, не связанной с категорией");
        testUnit = new Unit();
        testUnit.setName("TEST_UNIT");
        testUnit.setAbbreviation("TEST_ABBR");
        unitDAO.addUnit(testUnit);
        allUnits = unitDAO.getAllUnits();
        //
        unitDAO.deleteUnit(testUnit.getId());
        //
        actualUnits = unitDAO.getAllUnits();
        assertEquals(allUnits.size() - 1, actualUnits.size());
        actualUnit = unitDAO.getUnitById(testUnit.getId());
        assertNull(actualUnit);
    }

    @Test
    public void test_getUnitMap() {
        List<CatalogItem> itemList = catalogItemDAO.getSubTree(null);
        Set<Integer> categoryIdSet = new HashSet<Integer>();

        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryIdSet.add(item.getId());
            }
        }
        assertFalse(categoryIdSet.isEmpty());

        Map<Integer, Category> categoryMap = categoryDAO.getCategoryMap(categoryIdSet);

        Set<Integer> unitIdSet = new HashSet<Integer>();
        for (Category cat : categoryMap.values()) {
            unitIdSet.add(cat.getUnitId());
        }

        Map<Integer, Unit> unitMap = unitDAO.getUnitMap(unitIdSet);
        assertNotNull(unitMap);
        assertEquals(unitIdSet.size(), unitMap.size());
        assertTrue(unitIdSet.containsAll(unitMap.keySet()));
    }

    private void equalsUnits(Unit expectedUnit, Unit actualUnit) {
        assertEquals(expectedUnit.getId(), actualUnit.getId());
        assertEquals(expectedUnit.getName(), actualUnit.getName());
        assertEquals(expectedUnit.getAbbreviation(), actualUnit.getAbbreviation());
    }
}
