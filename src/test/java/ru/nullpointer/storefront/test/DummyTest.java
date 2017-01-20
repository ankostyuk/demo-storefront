package ru.nullpointer.storefront.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class DummyTest {

    private Logger logger = LoggerFactory.getLogger(DummyTest.class);
    //

    @Test
    public void testDummy() {
        logger.debug("test logger test");
        assertTrue(true);
    }
}
