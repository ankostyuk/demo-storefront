package ru.nullpointer.storefront.test.dao;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.dao.impl.OrdinalHelper;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.Param;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class OrdinalHelperTest {

    private Logger logger = LoggerFactory.getLogger(OrdinalHelperTest.class);
    //
    private static final String TABLE_NAME = "param";
    private static final String ID_COLUMN = "par_id";
    private static final String GROUP_COLUMN = "par_pg_id";
    private static final String ORDINAL_COLUMN = "par_ordinal";
    //
    @Resource
    private SqlMapClient sqlMapClient;
    @Resource
    private DataSource dataSource;
    //
    @Resource
    private ParamDAO paramDAO;

    @Test
    public void test_ordinalCRUD() {
        SqlMapClientTemplate template = new SqlMapClientTemplate(dataSource, sqlMapClient);

        OrdinalHelper helper = new OrdinalHelper(TABLE_NAME, ID_COLUMN, GROUP_COLUMN, ORDINAL_COLUMN);

        List<Param> paramList = getParamGroupList();
        Param p = paramList.get(0);

        logger.debug("param list size: {}", paramList.size());

        // CREATE
        Param newParam = initParam(p.getParamGroupId(), p.getParameterSetDescriptorId());
        paramDAO.insert(newParam);
        Integer paramId = newParam.getId();

        Integer ordinal = helper.updateOrdinal(template, paramId, -1);
        assertNotNull(ordinal);

        paramList = getParamGroupList();
        assertEquals(Integer.valueOf(paramList.size() - 1), ordinal);

        logger.debug("paramList: {}", paramList);

        // READ
        newParam = paramDAO.getParamById(paramId);
        Integer newOrdinal = newParam.getOrdinal();
        assertEquals(newOrdinal, ordinal);

        // UPDATE
        // переместить в середину, то есть вверх
        ordinal = helper.updateOrdinal(template, paramId, newOrdinal / 2);
        logger.debug("paramOrderList: {}", getParamOrderList());
        checkOrdinal(paramId, ordinal);

        // UPDATE
        // переместить в конец, то есть вниз
        ordinal = helper.updateOrdinal(template, paramId, paramList.size() - 1);
        logger.debug("paramOrderList: {}", getParamOrderList());
        checkOrdinal(paramId, ordinal);

        // UPDATE
        // переместить в начало, то есть вверх
        ordinal = helper.updateOrdinal(template, paramId, 0);
        logger.debug("paramOrderList: {}", getParamOrderList());
        checkOrdinal(paramId, ordinal);

        // UPDATE
        // оставить на месте
        newParam = paramDAO.getParamById(paramId);
        newOrdinal = newParam.getOrdinal();
        logger.debug("same ordinal start, newOrdinal: {}", newOrdinal);
        ordinal = helper.updateOrdinal(template, paramId, newOrdinal);
        logger.debug("same ordinal end, ordinal: {}", ordinal);

        assertEquals(newOrdinal, ordinal);
        checkOrdinal(paramId, ordinal);

        // DELETE
        helper.deleteOrdinal(template, paramId);
        newParam = paramDAO.getParamById(paramId);
        assertNull(newParam.getOrdinal());
        // RESTORE ordinal
        ordinal = helper.updateOrdinal(template, paramId, ordinal);
        
        paramDAO.delete(paramId);

        paramList = getParamGroupList();
        for (int i = 0; i < paramList.size(); i++) {
            assertEquals(Integer.valueOf(i), paramList.get(i).getOrdinal());
        }
    }

    private void checkOrdinal(Integer paramId, Integer ordinal) {
        Param newParam = paramDAO.getParamById(paramId);
        Integer newOrdinal = newParam.getOrdinal();
        assertEquals(newOrdinal, ordinal);

        List<Param> paramList = getParamGroupList();
        assertTrue(ordinal < paramList.size());
        for (int i = 0; i < paramList.size(); i++) {
            assertEquals(Integer.valueOf(i), paramList.get(i).getOrdinal());
        }
    }

    private Param initParam(Integer groupId, Integer psdId) {
        BooleanParam p = new BooleanParam();

        p.setBase(Boolean.TRUE);
        p.setColumnName("test_column_name");
        p.setDescription("test_description");
        p.setName("test_name");
        p.setOrdinal(null);
        p.setParamGroupId(groupId);
        p.setParameterSetDescriptorId(psdId);
        p.setTrueName("true");
        p.setFalseName("true");

        return p;
    }

    private List<Integer> getParamOrderList() {
        List<Integer> result = new ArrayList<Integer>();
        for (Param p : getParamGroupList()) {
            result.add(p.getOrdinal());
        }
        return result;
    }

    private List<Param> getParamGroupList() {
        Param p = paramDAO.getAllParams().get(0);
        List<Param> result = paramDAO.getParamsByParamGroupId(p.getParamGroupId());

        assertNotNull(result);
        assertTrue(!result.isEmpty());

        return result;
    }
}
