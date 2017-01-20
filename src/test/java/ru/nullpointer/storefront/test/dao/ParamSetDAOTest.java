package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
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
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.dao.ParamGroupDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.param.SelectParam;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ParamSetDAOTest {

    private Logger logger = LoggerFactory.getLogger(ParamSetDAOTest.class);
    //
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private ParamDAO paramDAO;
    @Resource
    private ParamGroupDAO paramGroupDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void test_ParamSetDescriptorCRUD() {
        final String tableName = "test_table_name";
        ParamSetDescriptor d = new ParamSetDescriptor();
        d.setTableName(tableName);
        d.setName("test descriptor");

        paramSetDAO.addParamSetDescriptor(d);
        Integer id = d.getId();
        assertNotNull(id);

        ParamSetDescriptor newDescriptor = paramSetDAO.getParamSetDescriptorById(id);
        assertEquals(id, newDescriptor.getId());
        assertEquals(tableName, newDescriptor.getTableName());

        paramSetDAO.deleteParamSetDescriptor(id);

        d = paramSetDAO.getParamSetDescriptorById(id);
        assertNull(d);
    }

    @Test
    public void test_ParamSetCRUD() {
        ParamSetDescriptor d = createDescriptor();

        Integer descriptorId = d.getId();

        ParamGroup pg = new ParamGroup();
        pg.setName("Тестовая группа параметров");
        pg.setOrdinal(0);
        pg.setParameterSetDescriptorId(descriptorId);

        paramGroupDAO.insert(pg);
        assertNotNull(pg.getId());

        ParamBuilder paramBuilder = new ParamBuilder()//
                .setParamSetDescriptorId(descriptorId)//
                .setParamGroupId(pg.getId())//
                .setDAOTestHelper(DAOTestHelper);

        Param p1 = paramBuilder.buildBooleanParam(1);
        paramDAO.insert(p1);
        assertNotNull(p1.getId());

        Param p2 = paramBuilder.buildNumberParam(2);
        paramDAO.insert(p2);
        assertNotNull(p2.getId());

        Param p3 = paramBuilder.buildSelectParam(3);
        paramDAO.insert(p3);
        assertNotNull(p3.getId());

        Param p4 = paramBuilder.buildBooleanParam(4);
        paramDAO.insert(p4);
        assertNotNull(p4.getId());

        // CREATE
        Map<Integer, Object> paramValueMap = new HashMap<Integer, Object>();
        paramValueMap.put(p1.getId(), Boolean.TRUE);
        paramValueMap.put(p2.getId(), BigDecimal.valueOf(42));
        paramValueMap.put(p3.getId(), 1001);
        paramValueMap.put(p4.getId(), null);

        Integer paramSetId = paramSetDAO.insert(descriptorId, paramValueMap);
        assertNotNull(paramSetId);

        // READ
        Map<Integer, Object> newParamValueMap = paramSetDAO.getParamSetById(descriptorId, paramSetId);
        logger.debug("newParamValueMap: {}", newParamValueMap);

        assertNotNull(newParamValueMap);
        assertParamSetEquals(paramValueMap, newParamValueMap);

        // UPDATE
        paramValueMap.put(p1.getId(), Boolean.FALSE);
        paramValueMap.put(p2.getId(), BigDecimal.valueOf(123));
        paramValueMap.put(p3.getId(), null);
        paramValueMap.put(p4.getId(), Boolean.TRUE);

        paramSetDAO.update(descriptorId, paramSetId, paramValueMap);

        newParamValueMap = paramSetDAO.getParamSetById(descriptorId, paramSetId);
        assertNotNull(newParamValueMap);
        assertParamSetEquals(paramValueMap, newParamValueMap);

        // DELETE
        paramSetDAO.delete(descriptorId, paramSetId);
        try {
            paramSetDAO.getParamSetById(descriptorId, paramSetId);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void test_getParamSetMap() {
        ParamSetDescriptor d = createDescriptor();

        Integer descriptorId = d.getId();

        ParamGroup pg = new ParamGroup();
        pg.setName("Тестовая группа параметров");
        pg.setOrdinal(0);
        pg.setParameterSetDescriptorId(descriptorId);

        paramGroupDAO.insert(pg);
        assertNotNull(pg.getId());

        ParamBuilder paramBuilder = new ParamBuilder()//
                .setParamSetDescriptorId(descriptorId)//
                .setParamGroupId(pg.getId())//
                .setDAOTestHelper(DAOTestHelper);

        Param p1 = paramBuilder.buildBooleanParam(1);
        paramDAO.insert(p1);
        assertNotNull(p1.getId());

        Param p2 = paramBuilder.buildNumberParam(2);
        paramDAO.insert(p2);
        assertNotNull(p2.getId());

        Param p3 = paramBuilder.buildSelectParam(3);
        paramDAO.insert(p3);
        assertNotNull(p3.getId());

        Param p4 = paramBuilder.buildBooleanParam(4);
        paramDAO.insert(p4);
        assertNotNull(p4.getId());

        Set<Integer> paramSetIdSet = new HashSet<Integer>();

        Map<Integer, Object> paramValueMap1 = new HashMap<Integer, Object>();
        paramValueMap1.put(p1.getId(), Boolean.TRUE);
        paramValueMap1.put(p2.getId(), BigDecimal.valueOf(42));
        paramValueMap1.put(p3.getId(), 1001);
        paramValueMap1.put(p4.getId(), null);

        Integer paramSetId1 = paramSetDAO.insert(descriptorId, paramValueMap1);
        assertNotNull(paramSetId1);
        paramSetIdSet.add(paramSetId1);

        Map<Integer, Object> paramValueMap2 = new HashMap<Integer, Object>();
        paramValueMap2.put(p1.getId(), Boolean.FALSE);
        paramValueMap2.put(p2.getId(), BigDecimal.valueOf(142));
        paramValueMap2.put(p3.getId(), 1002);
        paramValueMap2.put(p4.getId(), Boolean.TRUE);

        Integer paramSetId2 = paramSetDAO.insert(descriptorId, paramValueMap2);
        assertNotNull(paramSetId2);
        paramSetIdSet.add(paramSetId2);

        Map<Integer, Map<Integer, Object>> result = paramSetDAO.getParamSetMap(descriptorId, paramSetIdSet);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertParamSetEquals(paramValueMap1, result.get(paramSetId1));
        assertParamSetEquals(paramValueMap2, result.get(paramSetId2));
    }

    private void assertParamSetEquals(Map<Integer, Object> paramValueMap, Map<Integer, Object> newParamValueMap) {
        assertEquals(paramValueMap.size(), newParamValueMap.size());
        for (Integer paramId : paramValueMap.keySet()) {
            Object value = paramValueMap.get(paramId);
            Object newValue = newParamValueMap.get(paramId);
            if (value == null) {
                assertNull(newValue);
            } else {
                assertEquals(value.getClass(), newValue.getClass());
                assertTrue(((Comparable) value).compareTo((Comparable) newValue) == 0);
            }
        }
    }

    private ParamSetDescriptor createDescriptor() {
        ParamSetDescriptor d = new ParamSetDescriptor();
        d.setTableName("test_table_name");
        d.setName("test descriptor");

        paramSetDAO.addParamSetDescriptor(d);
        assertNotNull(d.getId());
        return d;
    }

    private static class ParamBuilder {

        private Integer paramSetDescriptorId;
        private Integer paramGroupId;
        private DAOTestHelper DAOTestHelper;

        ParamBuilder setParamSetDescriptorId(Integer paramDescriptorId) {
            this.paramSetDescriptorId = paramDescriptorId;
            return this;
        }

        ParamBuilder setParamGroupId(Integer paramGroupId) {
            this.paramGroupId = paramGroupId;
            return this;
        }

        ParamBuilder setDAOTestHelper(DAOTestHelper DAOTestHelper) {
            this.DAOTestHelper = DAOTestHelper;
            return this;
        }

        Param buildBooleanParam(int index) {
            BooleanParam p = new BooleanParam();
            initParam(p, index);

            p.setTrueName("true");
            p.setFalseName("false");
            return p;
        }

        Param buildNumberParam(int index) {
            NumberParam p = new NumberParam();
            initParam(p, index);

            p.setMinValue(BigDecimal.valueOf(0));
            p.setMaxValue(BigDecimal.valueOf(10));
            p.setPrecision(0);
            p.setUnitId(DAOTestHelper.createUnit().getId());
            return p;
        }

        Param buildSelectParam(int index) {
            SelectParam p = new SelectParam();
            initParam(p, index);
            return p;
        }

        private void initParam(Param p, int index) {
            p.setParameterSetDescriptorId(paramSetDescriptorId);
            p.setParamGroupId(paramGroupId);
            p.setName("Тестовый параметр №" + index);
            p.setDescription("Тестовое описание №" + index);
            p.setColumnName("column_name_" + index);
            p.setOrdinal(index);
            p.setBase(index % 2 == 0);
        }
    }
}
