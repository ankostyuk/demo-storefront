package ru.nullpointer.storefront.test.dao;

import java.util.ArrayList;
import java.util.List;
import ru.nullpointer.storefront.test.AssertUtils;
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
import ru.nullpointer.storefront.dao.CartDAO;
import ru.nullpointer.storefront.domain.Cart;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CartDAOTest {

    private Logger logger = LoggerFactory.getLogger(CartDAOTest.class);
    //
    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private CartDAO cartDAO;

    @Test
    public void test_CRUD() {
        Integer sessionId = DAOTestHelper.createSession().getId();

        // CREATE
        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setName("test cart");
        cart.setDescription("test description");

        cartDAO.insert(cart);
        assertNotNull(cart.getId());
        Integer cartId = cart.getId();

        // READ
        Cart cart2 = cartDAO.getCart(cartId);
        assertCartEquals(cart, cart2);

        // UPDATE
        cart.setName(cart.getName() + " UPD");
        cart.setDescription(cart.getDescription() + " UPD");
        cartDAO.updateInfo(cart);

        cart2 = cartDAO.getCart(cartId);
        assertCartEquals(cart, cart2);

        // DELETE
        cartDAO.delete(cartId);
        cart2 = cartDAO.getCart(cartId);
        assertNull(cart2);
    }

    @Test
    public void test_getList() {
        Integer sessionId = DAOTestHelper.createSession().getId();

        List<Cart> cartList = new ArrayList<Cart>();
        for (int i = 0; i < 10; i++) {
            Cart cart = new Cart();
            cart.setSessionId(sessionId);
            cart.setName("test cart " + i);
            cart.setDescription("test description " + i);
            cartDAO.insert(cart);

            cartList.add(cart);
        }

        List<Cart> result = cartDAO.getCartList(sessionId);
        assertNotNull(result);
        assertEquals(cartList.size(), result.size());
        for (int i = 0; i < cartList.size(); i++) {
            assertCartEquals(cartList.get(i), result.get(i));
        }
    }

    private void assertCartEquals(Cart c, Cart c2) {
        AssertUtils.assertPropertiesEquals(c, c2,
                "id", "sessionId",
                "name", "description");
    }
}
