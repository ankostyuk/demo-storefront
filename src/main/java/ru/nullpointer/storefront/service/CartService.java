package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CartDAO;
import ru.nullpointer.storefront.dao.CartItemDAO;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.CartItem;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class CartService {

    private static final int MAX_CART_LIST_SIZE = 10;
    //
    @Resource
    private MatchService matchService;
    @Resource
    private SessionService sessionService;
    @Resource
    private CartDAO cartDAO;
    @Resource
    private CartItemDAO cartItemDAO;
    @Resource
    private TimeService timeService;

    @Transactional
    public List<Cart> getCartList() {
        List<Cart> cartList = Collections.emptyList();
        Integer sessionId = sessionService.getSessionId(false);
        if (sessionId != null) {
            cartList = cartDAO.getCartList(sessionId);
        }
        return cartList;
    }

    @Transactional
    public Cart getCart(Integer cartId) {
        return getCartInternal(cartId);
    }

    @Transactional
    public boolean storeCart(Cart cart) {
        Assert.notNull(cart);

        Integer sessionId = sessionService.getSessionId(true);
        if (sessionId == null) {
            return false; // TODO Exception?
        }

        if (cartDAO.getCartList(sessionId).size() >= MAX_CART_LIST_SIZE) {
            return false;
        }

        cart.setSessionId(sessionId);
        FieldUtils.nullifyIfEmpty(cart, "description");

        cartDAO.insert(cart);
        return true;
    }

    @Transactional
    public void updateCart(Cart cart) {
        Assert.notNull(cart);
        if (getCartInternal(cart.getId()) == null) {
            return;
        }
        cartDAO.updateInfo(cart);
    }

    @Transactional
    public boolean deleteCart(Integer cartId) {
        if (getCartInternal(cartId) == null) {
            return false;
        }
        cartDAO.delete(cartId);
        return true;
    }

    @Transactional
    public Map<Integer, List<CartItem>> getCartItemMap() {
        Map<Integer, List<CartItem>> itemMap = Collections.emptyMap();

        Integer sessionId = sessionService.getSessionId(false);
        if (sessionId != null) {
            itemMap = cartItemDAO.getCartItemMap(sessionId);
        }

        if (!itemMap.isEmpty()) {
            filterAccessibleMatches(itemMap);
        }
        return itemMap;
    }

    @Transactional
    public boolean addToCart(Integer cartId, Match match) {
        Assert.notNull(match);

        Cart cart = getCartInternal(cartId);
        if (cart == null) {
            return false; // TODO Exception?
        }

        List<CartItem> itemList = cartItemDAO.getCartItemMap(cart.getSessionId()).get(cart.getId());
        if (itemList != null) {
            if (itemList.size() >= Cart.MAX_CART_ITEM_LIST_SIZE) {
                // список уже максимального размера
                return false;
            } else {
                // проверить - может уже в списке?
                for (CartItem item : itemList) {
                    if ((item.getMatch().equals(match))) {
                        return true;
                    }
                }
            }
        }

        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setDateAdded(timeService.now());
        item.setMatch(match);

        cartItemDAO.insert(item);
        
        return true;
    }

    @Transactional
    public boolean deleteFromCart(Integer cartId, Match match) {
        Assert.notNull(match);

        Cart cart = getCartInternal(cartId);
        if (cart == null) {
            return false; // TODO Exception?
        }

        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setMatch(match);

        return cartItemDAO.delete(item);
    }

    private void filterAccessibleMatches(Map<Integer, List<CartItem>> itemMap) {
        // оставить только доступные совпадения
        // TODO: избавиться от цикла
        for (List<CartItem> itemList : itemMap.values()) {
            Set<Match> matchSet = new HashSet<Match>(itemList.size());
            for (CartItem item : itemList) {
                matchSet.add(item.getMatch());
            }
            Set<Match> accessibleSet = matchService.getAccessibleMatchSubset(matchSet, null, false);
            Iterator<CartItem> it = itemList.iterator();
            while (it.hasNext()) {
                CartItem item = it.next();
                if (!accessibleSet.contains(item.getMatch())) {
                    it.remove();
                    cartItemDAO.delete(item);
                }
            }
        }
    }

    private Cart getCartInternal(Integer cartId) {
        Assert.notNull(cartId);
        Integer sessionId = sessionService.getSessionId(false);
        if (sessionId != null) {
            Cart cart = cartDAO.getCart(cartId);
            if (cart != null && sessionId.equals(cart.getSessionId())) {
                return cart;
            }
        }
        return null;
    }
}
