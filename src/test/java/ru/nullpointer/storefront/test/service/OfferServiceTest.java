package ru.nullpointer.storefront.test.service;

import javax.annotation.Resource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.service.OfferService;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class OfferServiceTest {

    @Resource
    private OfferService offerService;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private RegionDAO regionDAO;

    @Test
    public void test_stub() {
        assertTrue(true);
    }
}
