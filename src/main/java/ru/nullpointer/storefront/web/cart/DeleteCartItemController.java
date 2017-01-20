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
@RequestMapping("/cart/{cartId}/item/delete")
public class DeleteCartItemController {

    @Resource
    private CartService cartService;
    @Resource
    private CartHelper cartHelper;
    @Resource
    private SettingsService settingsService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("cartId") Integer cartId,
            @RequestParam("id") String[] idValues, ModelMap model) {

        model.addAttribute("cart", cartHelper.getCart(cartId));

        Region region = RegionHelper.getUserRegion(settingsService.getSettings());
        model.addAttribute("matchesToDelete", cartHelper.parseMatchList(idValues, region));

        return "cart/item/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("cartId") Integer cartId,
            @RequestParam("id") String[] idValues,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {

        Cart cart = cartHelper.getCart(cartId);

        boolean success = false;

        // Список может быть дефолтным - проверяем
        cartId = cart.getId();
        if (cartId != null) {
            MatchParser parser = new MatchParser();
            for (Match m : parser.parse(idValues, Cart.MAX_CART_ITEM_LIST_SIZE)) {
                success = cartService.deleteFromCart(cartId, m);
                if (!success) {
                    break;
                }
            }
        }

        MatchActionInfo actionInfo = new MatchActionInfo(ActionInfo.Type.MATCH_DELETE_FROM_CART, idValues);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return cartHelper.buildCartItemActionViewName(cartId, actionInfo, inline, "cart/item/delete", model);
    }
}
