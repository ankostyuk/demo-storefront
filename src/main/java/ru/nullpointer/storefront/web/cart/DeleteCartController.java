package ru.nullpointer.storefront.web.cart;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ActionInfo;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/cart/delete/{cartId}")
public class DeleteCartController {

    @Resource
    private CartService cartService;
    @Resource
    private CartHelper cartHelper;

    @ModelAttribute("cart")
    public Cart getCart(@PathVariable("cartId") Integer cartId) {
        Cart cart = cartService.getCart(cartId);
        if (cart == null) {
            throw new NotFoundException();
        }
        return cart;
    }

    @InitBinder("cart")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "cart/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("cart") Cart cart,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {
        boolean success = cartService.deleteCart(cart.getId());
        
        CartActionInfo actionInfo = new CartActionInfo(ActionInfo.Type.CART_DELETE, cart);
        actionInfo.setSuccess(success);
        if (success) {
            cart.setId(null);
        } else {
            actionInfo.setRedirect(redirect);
        }

        return cartHelper.buildCartActionViewName(actionInfo, inline, "cart/delete", model);
    }
}
