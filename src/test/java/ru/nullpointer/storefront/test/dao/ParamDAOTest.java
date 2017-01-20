package ru.nullpointer.storefront.test.dao;

import java.math.BigDecimal;
import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.SelectParam;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ParamDAOTest {

    private Logger logger = LoggerFactory.getLogger(ParamDAOTest.class);
    //
    @Resource
    private ParamDAO paramDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void test_getAllParams() {
        List<Param> paramList = paramDAO.getAllParams();
        assertNotNull(paramList);
        assertFalse(paramList.isEmpty());

        for (Param p : paramList) {
            logger.debug("param: {}", p);
        }
    }

    @Test
    public void test_allDelete() {
        List<Param> paramList = paramDAO.getAllParams();
        assertNotNull(paramList);
        assertFalse(paramList.isEmpty());

        for (Param p : paramList) {
            paramDAO.delete(p.getId());
        }

        paramList = paramDAO.getAllParams();
        assertNotNull(paramList);
        assertTrue(paramList.isEmpty());
    }

    @Test
    public void test_getParamById() {
        List<Param> paramList = paramDAO.getAllParams();
        assertFalse(paramList.isEmpty());

        for (Param p : paramList) {
            Integer id = p.getId();
            Param p1 = paramDAO.getParamById(id);
            assertParamEquals(p, p1);
        }
    }

    @Test
    public void test_BOOLEAN_CRUD() {
        BooleanParam p = new BooleanParam();

        initParam(p);

        p.setTrueName("true");
        p.setFalseName("false");

        // CREATE
        paramDAO.insert(p);
        assertNotNull(p.getId());
        checkOrdinal(p);

        // READ
        Param newParam = paramDAO.getParamById(p.getId());
        assertNotNull(newParam);
        assertParamEquals(p, newParam);

        // UPDATE
        Integer ordinal = p.getOrdinal();
        modifyParam(p);

        p.setTrueName("UPD:" + p.getTrueName());
        p.setFalseName("UPD:" + p.getFalseName());

        paramDAO.updateInfo(p);
        newParam = paramDAO.getParamById(p.getId());
        assertParamEqualsUpdate(p, newParam);
        assertEquals(ordinal, newParam.getOrdinal());

        // DELETE
        paramDAO.delete(p.getId());

        newParam = paramDAO.getParamById(p.getId());
        assertNull(newParam);

        checkAfterDeleteOrdinal(p.getParamGroupId());
    }

    @Test
    public void test_NUMBER_CRUD() {
        NumberParam p = new NumberParam();

        initParam(p);

        p.setMinValue(BigDecimal.valueOf(10));
        p.setMaxValue(BigDecimal.valueOf(1000));
        p.setPrecision(1);
        p.setUnitId(DAOTestHelper.createUnit().getId());

        // CREATE
        paramDAO.insert(p);
        assertNotNull(p.getId());
        checkOrdinal(p);

        // READ
        Param newParam = paramDAO.getParamById(p.getId());
        assertNotNull(newParam);
        assertParamEquals(p, newParam);

        // UPDATE
        Integer ordinal = p.getOrdinal();
        modifyParam(p);

        p.setMinValue(p.getMinValue().multiply(BigDecimal.valueOf(2)));
        p.setMaxValue(p.getMaxValue().multiply(BigDecimal.valueOf(2)));
        p.setPrecision(p.getPrecision() * 2);
        p.setUnitId(DAOTestHelper.createUnit().getId());

        paramDAO.updateInfo(p);
        newParam = paramDAO.getParamById(p.getId());
        assertParamEqualsUpdate(p, newParam);
        assertEquals(ordinal, newParam.getOrdinal());

        // DELETE
        paramDAO.delete(p.getId());

        newParam = paramDAO.getParamById(p.getId());
        assertNull(newParam);

        checkAfterDeleteOrdinal(p.getParamGroupId());
    }

    @Test
    public void test_invalidType() {
        BooleanParam p = new BooleanParam();

        initParam(p);

        p.setTrueName("true");
        p.setFalseName("false");

        // INSERT
        p.setType(Param.Type.NUMBER);
        try {
            paramDAO.insert(p);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        p.setType(Param.Type.BOOLEAN);
        paramDAO.insert(p);

        // UPDATE
        p.setType(Param.Type.SELECT);
        try {
            paramDAO.updateInfo(p);
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    private void checkOrdinal(Param p) {
        List<Param> paramList = paramDAO.getParamsByParamGroupId(p.getParamGroupId());

        Integer ordinal = p.getOrdinal();
        assertNotNull(ordinal);

        assertTrue(ordinal < paramList.size());
        for (int i = 0; i < paramList.size(); i++) {
            assertEquals(Integer.valueOf(i), paramList.get(i).getOrdinal());
        }
        assertEquals(p.getId(), paramList.get(ordinal).getId());
    }

    private void checkAfterDeleteOrdinal(Integer paramGroupId) {
        List<Param> paramList = paramDAO.getParamsByParamGroupId(paramGroupId);
        for (int i = 0; i < paramList.size(); i++) {
            assertEquals(Integer.valueOf(i), paramList.get(i).getOrdinal());
        }
    }

    private void modifyParam(Param p) {
        p.setName("UPD:" + p.getName());
        p.setDescription("UPD:" + p.getDescription());
        p.setColumnName("UPD:" + p.getColumnName());
        p.setOrdinal(2 * p.getOrdinal());
        p.setBase(!p.getBase());
    }

    private void initParam(Param p) {
        Param firstParam = paramDAO.getAllParams().get(0);
        Integer psdId = firstParam.getParameterSetDescriptorId();
        Integer pgId = firstParam.getParamGroupId();

        p.setParameterSetDescriptorId(psdId);
        p.setParamGroupId(pgId);
        p.setName("Тестовый параметр");
        p.setDescription("Тестовое описание");
        p.setColumnName("test_column_name");
        p.setOrdinal(null);
        p.setBase(true);
    }

    private void assertParamEquals(Param p1, Param p2) {
        assertTrue(p2.getColumnName().startsWith(p1.getColumnName()));
        assertEquals(p1.getParameterSetDescriptorId(), p2.getParameterSetDescriptorId());
        assertEquals(p1.getParamGroupId(), p2.getParamGroupId());
        assertEquals(p1.getType(), p2.getType());
        assertEquals(p1.getOrdinal(), p2.getOrdinal());

        assertParamEqualsUpdate(p1, p2);
    }

    private void assertParamEqualsUpdate(Param p1, Param p2) {
        assertEquals(p1.getClass(), p2.getClass());
        assertEquals(p1.getId(), p2.getId());
        assertEquals(p1.getName(), p2.getName());
        assertEquals(p1.getDescription(), p2.getDescription());
        assertEquals(p1.getBase(), p2.getBase());

        switch (p1.getType()) {
            case BOOLEAN:
                assertEquals(BooleanParam.class, p1.getClass());
                BooleanParam bp1 = (BooleanParam) p1;
                BooleanParam bp2 = (BooleanParam) p2;
                assertEquals(bp1.getTrueName(), bp2.getTrueName());
                assertEquals(bp1.getFalseName(), bp2.getFalseName());
                break;

            case NUMBER:
                assertEquals(NumberParam.class, p1.getClass());
                NumberParam np1 = (NumberParam) p1;
                NumberParam np2 = (NumberParam) p2;
                assertTrue(np1.getMinValue().compareTo(np2.getMinValue()) == 0);
                assertTrue(np1.getMaxValue().compareTo(np2.getMaxValue()) == 0);
                assertTrue(np1.getPrecision().compareTo(np2.getPrecision()) == 0);
                assertEquals(np1.getUnitId(), np2.getUnitId());
                break;

            case SELECT:
                assertEquals(SelectParam.class, p1.getClass());
                break;

            default:
                fail("Неизвестный тип параметра \"" + p1.getType() + "\"");
        }
    }
}

