package ru.nullpointer.storefront.test.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.CartDAO;
import ru.nullpointer.storefront.dao.CartItemDAO;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.CartItem;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.test.AssertUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CartItemDAOTest {

    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private CartDAO cartDAO;
    @Resource
    private CartItemDAO cartItemDAO;

    @Test
    public void test_CRD() {
        Integer sessionId = DAOTestHelper.createSession().getId();

        Cart cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setName("test cart");
        cart.setDescription("test description");

        cartDAO.insert(cart);
        assertNotNull(cart.getId());
        Integer cartId = cart.getId();

        Date now = new Date();
        List<CartItem> itemList = new ArrayList<CartItem>();
        for (int i = 0; i < 10; i++) {
            CartItem item = new CartItem();
            item.setCartId(cartId);
            item.setDateAdded(DateUtils.addSeconds(now, i));
            item.setMatch(new Match(i % 2 == 0 ? Match.Type.OFFER : Match.Type.MODEL, i + 1));
            itemList.add(item);
        }

        // CREATE
        for (CartItem item : itemList) {
            cartItemDAO.insert(item);
        }

        // READ
        Map<Integer, List<CartItem>> itemMap = cartItemDAO.getCartItemMap(sessionId);
        assertNotNull(itemMap);
        assertEquals(1, itemMap.size());

        List<CartItem> list = itemMap.get(cartId);
        assertNotNull(list);
        assertEquals(itemList.size(), list.size());
        for (int i = 0; i < itemList.size(); i++) {
            assertCartItemEquals(itemList.get(i), list.get(i));
        }

        // DELETE
        CartItem removed = itemList.remove(0);
        cartItemDAO.delete(removed);

        itemMap = cartItemDAO.getCartItemMap(sessionId);
        list = itemMap.get(cartId);
        assertNotNull(list);
        assertEquals(itemList.size(), list.size());
        for (int i = 0; i < itemList.size(); i++) {
            assertCartItemEquals(itemList.get(i), list.get(i));
        }
    }

    private void assertCartItemEquals(CartItem i1, CartItem i2) {
        AssertUtils.assertPropertiesEquals(i1, i2, "cartId", "dateAdded", "match");
    }
}
