package ru.nullpointer.storefront.web.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.CartItem;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.MatchActionInfo;
import ru.nullpointer.storefront.web.support.MatchParser;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
class CartHelper {

    @Resource
    private MatchService matchService;
    @Resource
    private CartService cartService;
    @Resource
    private MessageSource messageSource;

    Cart getCart(Integer cartId) {
        Cart cart = null;
        if (cartId != null) {
            cart = cartService.getCart(cartId);
            if (cart == null) {
                throw new NotFoundException();
            }
        } else {
            List<Cart> cartList = cartService.getCartList();
            if (cartList.isEmpty()) {
                cart = new Cart();
                cart.setName(messageSource.getMessage("ui.cart.default.name", null, null));
            } else {
                cart = cartList.get(0);
            }
        }
        return cart;
    }

    Map<Match, AbstractMatch> getCartMatchMap(List<CartItem> itemList, Region region) {
        if (itemList == null || itemList.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Match> matchList = new ArrayList<Match>(itemList.size());
        for (CartItem item : itemList) {
            matchList.add(item.getMatch());
        }
        return matchService.buildMatchResultMap(matchList, region);
    }

    List<AbstractMatch> parseMatchList(String[] idValues, Region region) {
        List<Match> parsedList = new MatchParser().parse(idValues, Cart.MAX_CART_ITEM_LIST_SIZE);
        List<Match> matchList = getAccessibleMatchList(parsedList);
        return matchService.buildMatchResultList(matchList, region);
    }

    String buildCartItemActionViewName(Integer cartId, MatchActionInfo actionInfo, boolean inline, String noSuccessViewName, ModelMap model) {
        Assert.notNull(actionInfo);
        if (inline) {
            model.addAttribute("_actionInfo", actionInfo);
            model.addAttribute("_cartId", cartId);
            return "match-cart-info";
        } else {
            if (actionInfo.isSuccess()) {
                if (StringUtils.isBlank(actionInfo.getRedirect())) {
                    return cartId != null ? ("redirect:/cart/" + cartId) : "redirect:/cart";
                }
                return "redirect:" + actionInfo.getRedirect();
            } else {
                model.addAttribute("_actionInfo", actionInfo);
                model.addAttribute("_cartId", cartId);
                return noSuccessViewName;
            }
        }
    }

    String buildCartActionViewName(CartActionInfo actionInfo, boolean inline, String noSuccessViewName, ModelMap model) {
        Assert.notNull(actionInfo);
        if (inline) {
            model.addAttribute("_actionInfo", actionInfo);
            return "cart-action-info";
        } else {
            if (actionInfo.isSuccess()) {
                if (StringUtils.isBlank(actionInfo.getRedirect())) {
                    return actionInfo.getCart().getId() != null ? ("redirect:/cart/" + actionInfo.getCart().getId()) : "redirect:/cart";
                }
                return "redirect:" + actionInfo.getRedirect();
            } else {
                model.addAttribute("_actionInfo", actionInfo);
                return noSuccessViewName;
            }
        }
    }

    private List<Match> getAccessibleMatchList(List<Match> matchList) {
        Set<Match> accessibleSet = matchService.getAccessibleMatchSubset(new HashSet<Match>(matchList), null, false);
        List<Match> accessibleList = new ArrayList<Match>(accessibleSet.size());
        for (Match m : matchList) {
            if (accessibleSet.contains(m)) {
                accessibleList.add(m);
            }
        }
        return accessibleList;
    }
}
