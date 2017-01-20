package ru.nullpointer.storefront.test.service.search;



import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.nullpointer.storefront.service.search.SearchLifecycle;

/**
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
//@TransactionConfiguration
//@Transactional
public class CompanySearchTest {

    private Logger logger = LoggerFactory.getLogger(CompanySearchTest.class);
    //

    //
    private static final boolean SKIP_CREATE_INDEX_TEST = false;
    private static final boolean SKIP_INDEX_TEST = false;
    //

    @Resource
    private SearchTestHelper searchTestHelper;
    @Resource
    private SearchLifecycle searchLifecycle;

    @Test
    public void testCreateIndex() throws Exception {
        if (SKIP_CREATE_INDEX_TEST) return;
        
        logger.debug("TEST createIndex start");
        searchLifecycle.createCompanyIndex();
        //
        searchTestHelper.waitingForCompleteTasks();
        //
        int docCount = searchLifecycle.getCompanyIndexDocCount();
        logger.debug("TEST createIndex finish, indexed doc count={}", docCount);
    }

}
