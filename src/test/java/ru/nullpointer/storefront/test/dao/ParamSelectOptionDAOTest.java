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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.ParamDAO;
import ru.nullpointer.storefront.dao.ParamSelectOptionDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ParamSelectOptionDAOTest {

    private Logger logger = LoggerFactory.getLogger(ParamSelectOptionDAOTest.class);
    //
    @Resource
    private ParamSelectOptionDAO paramSelectOptionDAO;
    @Resource
    private DAOTestHelper DAOTEstHelper;
    @Resource
    private ParamDAO paramDAO;

    @Test
    public void test_() {
        Category cat = DAOTEstHelper.getParametrizedCategory();

        Set<Integer> paramIdSet = new HashSet<Integer>();

        List<Param> paramList = paramDAO.getParamListByDescriptorId(cat.getParameterSetDescriptorId());
        for (Param p : paramList) {
            if (p.getType() == Param.Type.SELECT) {
                paramIdSet.add(p.getId());
            }
        }
        assertFalse(paramIdSet.isEmpty());

        Map<Integer, List<ParamSelectOption>> selectOptionMap = paramSelectOptionDAO.getParamSelectOptionMap(paramIdSet);
        assertEquals(paramIdSet.size(), selectOptionMap.size());
        for (Integer paramId : selectOptionMap.keySet()) {
            assertTrue(paramIdSet.contains(paramId));
            List<ParamSelectOption> optionList = selectOptionMap.get(paramId);
            assertNotNull(optionList);

            List<ParamSelectOption> optionList2 = paramSelectOptionDAO.getParamSelectOptionsByParamId(paramId);
            assertEquals(optionList2.size(), optionList.size());
            for (int i = 0; i < optionList2.size(); i++) {
                assertOptionEquals(optionList2.get(i), optionList.get(i));
            }
        }
    }

    private void assertOptionEquals(ParamSelectOption o1, ParamSelectOption o2) {
        assertEquals(o1.getId(), o2.getId());
        assertEquals(o1.getName(), o2.getName());
        assertEquals(o1.getOrdinal(), o2.getOrdinal());
        assertEquals(o1.getParamId(), o2.getParamId());
    }
}
