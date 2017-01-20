package ru.nullpointer.storefront.web.cart;

import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.support.ActionInfo;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/cart/add")
public class AddCartController {

    @Resource
    private CartService cartService;
    @Resource
    private CartHelper cartHelper;
    @Resource
    private BeanValidator validator;

    @ModelAttribute("cart")
    public Cart getCart() {
        return new Cart();
    }

    @InitBinder("cart")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "description");
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet() {
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("cart") Cart cart, BindingResult result,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {
        validator.validate(cart, result);
        if (result.hasErrors()) {
            return "cart/add";
        }

        boolean success = cartService.storeCart(cart);
        CartActionInfo actionInfo = new CartActionInfo(ActionInfo.Type.CART_ADD, cart);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        String view = cartHelper.buildCartActionViewName(actionInfo, inline, "cart/add", model);

        // "Предлагаем" выбор созданного списка покупок
        if (StringUtils.isNotBlank(redirect) && success && cart.getId() != null) {
            // Когда список покупок создается при добавлении match в список покупок
            if (StringUtils.startsWith(redirect, "/cart/item/add") && !StringUtils.contains(redirect, "cartId=")) {
                model.addAttribute("cartId", cart.getId());
            } else
            // Когда список покупок создается при перемещении match в список покупок
            if (Pattern.matches("^/cart/[^/]+/item/move[\\s\\S]*", redirect) && !StringUtils.contains(redirect, "toCartId=")) {
                model.addAttribute("toCartId", cart.getId());
            }
        }
        
        return view;
    }
}
