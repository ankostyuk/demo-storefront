package ru.nullpointer.storefront.test.dao;

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
import ru.nullpointer.storefront.dao.ParamGroupDAO;
import ru.nullpointer.storefront.domain.param.ParamGroup;

/**
 *
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ParamGroupDAOTest {

    private Logger logger = LoggerFactory.getLogger(ParamGroupDAOTest.class);
    //
    @Resource
    private ParamGroupDAO paramGroupDAO;

    @Test
    @Rollback(true)
    public void testMethodGetAllParamGroup() {
        List<ParamGroup> allParamGroup = paramGroupDAO.getAllParamGroups();
        assertNotNull(allParamGroup);
        //assertTrue(allParamGroup.size() > 0);
    }
    
}
