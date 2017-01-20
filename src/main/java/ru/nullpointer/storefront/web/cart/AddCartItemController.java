package ru.nullpointer.storefront.web.cart;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
@RequestMapping("/cart/item/add")
public class AddCartItemController {

    private Logger logger = LoggerFactory.getLogger(CartController.class);
    //
    @Resource
    private CartService cartService;
    @Resource
    private CartHelper cartHelper;
    @Resource
    private SettingsService settingsService;
    @Resource
    private MessageSource messageSource;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @RequestParam(value = "cartId", required = false) Integer cartId,
            @RequestParam("id") List<String> idValuesList, ModelMap model) {
        // TODO <code>@RequestParam("id") String[] idValues</code> - не работает при id=*&id=*...&id=*,
        // переписать на <code>@RequestParam("id") List<String> idValues</code>,
        // просмореть все аналогичные места.
        // Переписать зависимые методы с String[] на List<String>.
        String[] idValues = idValuesList.toArray(new String[idValuesList.size()]);

        model.addAttribute("cart", cartHelper.getCart(cartId));
        model.addAttribute("cartList", cartService.getCartList());

        Region region = RegionHelper.getUserRegion(settingsService.getSettings());
        model.addAttribute("matchesToAdd", cartHelper.parseMatchList(idValues, region));

        return "cart/item/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @RequestParam(value = "cartId", required = false) Integer cartId,
            @RequestParam("id") List<String> idValuesList,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {
        // TODO см. handleGet
        String[] idValues = idValuesList.toArray(new String[idValuesList.size()]);

        Cart cart = cartHelper.getCart(cartId);
        if (cart.getId() == null) {
            // новый список
            cartService.storeCart(cart);
        }

        boolean success = false;

        // новый список мог не сохраниться - проверяем
        cartId = cart.getId();
        if (cartId != null) {
            MatchParser parser = new MatchParser();
            for (Match m : parser.parse(idValues, Cart.MAX_CART_ITEM_LIST_SIZE)) {
                success = cartService.addToCart(cartId, m);
                if (!success) {
                    break;
                }
            }
        }

        MatchActionInfo actionInfo = new MatchActionInfo(ActionInfo.Type.MATCH_ADD_TO_CART, idValues);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return cartHelper.buildCartItemActionViewName(cartId, actionInfo, inline, "cart/item/add", model);
    }
}
