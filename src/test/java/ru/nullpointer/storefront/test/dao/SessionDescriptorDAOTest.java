package ru.nullpointer.storefront.test.dao;

import ru.nullpointer.storefront.test.AssertUtils;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.SessionDescriptorDAO;
import ru.nullpointer.storefront.domain.SessionDescriptor;
import ru.nullpointer.storefront.util.RandomUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class SessionDescriptorDAOTest {

    private Logger logger = LoggerFactory.getLogger(SessionDescriptorDAOTest.class);
    //
    @Resource
    private SessionDescriptorDAO sessionDescriptorDAO;

    @Test
    public void test_CRT() {
        String sessionId = RandomUtils.generateRandomString(32,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER,
                RandomUtils.ASCII_UPPER);

        SessionDescriptor sd = new SessionDescriptor();
        sd.setSessionId(sessionId);
        sd.setTouchDate(new Date());

        // CREATE
        sessionDescriptorDAO.insert(sd);
        assertNotNull(sd.getId());

        // READ
        SessionDescriptor sd2 = sessionDescriptorDAO.getDescriptorBySessionId(sessionId);
        AssertUtils.assertPropertiesEquals(sd, sd2, "id", "sessionId", "touchDate");

        // TOUCH
        Date future = DateUtils.addDays(new Date(), 1);
        sessionDescriptorDAO.touch(sessionId, future);

        sd = sessionDescriptorDAO.getDescriptorBySessionId(sessionId);
        assertEquals(future, sd.getTouchDate());

        // TOUCH несуществующий ИД - не должно отваливаться
        sessionDescriptorDAO.touch("несуществующий ИД", future);
    }
}
