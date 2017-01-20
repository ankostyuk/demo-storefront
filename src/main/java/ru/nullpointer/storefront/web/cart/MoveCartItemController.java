package ru.nullpointer.storefront.web.cart;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.service.SettingsService;
import ru.nullpointer.storefront.web.support.ActionInfo;
import ru.nullpointer.storefront.web.support.MatchActionInfo;
import ru.nullpointer.storefront.web.support.MatchParser;
import ru.nullpointer.storefront.web.support.RegionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/cart/{cartId}/item/move")
public class MoveCartItemController {

    @Resource
    private CartService cartService;
    @Resource
    private CartHelper cartHelper;
    @Resource
    private SettingsService settingsService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("cartId") Integer cartId,
            @RequestParam(value = "toCartId", required = false) Integer toCartId,
            @RequestParam("id") String[] idValues, ModelMap model) {

        Cart cart = cartHelper.getCart(cartId);
        model.addAttribute("cart", cart);
        model.addAttribute("toCartId", toCartId);
        model.addAttribute("cartList", cartService.getCartList());

        Region region = RegionHelper.getUserRegion(settingsService.getSettings());
        model.addAttribute("matchesToMove", cartHelper.parseMatchList(idValues, region));

        return "cart/item/move";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("cartId") Integer cartId,
            @RequestParam(value = "toCartId", required = false) Integer toCartId,
            @RequestParam("id") String[] idValues,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {

        boolean success = false;

        MatchParser parser = new MatchParser();
        for (Match m : parser.parse(idValues, Cart.MAX_CART_ITEM_LIST_SIZE)) {
            if (cartService.addToCart(toCartId, m)) {
                success = cartService.deleteFromCart(cartId, m);
                if (!success) {
                    break;
                }
            }
        }

        MatchActionInfo actionInfo = new MatchActionInfo(ActionInfo.Type.MATCH_MOVE_TO_CART, idValues);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return cartHelper.buildCartItemActionViewName(cartId, actionInfo, inline, "cart/item/move", model);
    }
}
