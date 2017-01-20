package ru.nullpointer.storefront.web.cart;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/cart/edit/{cartId}")
public class EditCartController {

    @Resource
    private CartService cartService;
    @Resource
    private BeanValidator validator;

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
        binder.setAllowedFields("name", "description");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "cart/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("cart") Cart cart, BindingResult result) {
        validator.validate(cart, result);
        if (result.hasErrors()) {
            return "cart/edit";
        }

        cartService.updateCart(cart);

        return "redirect:/cart/" + cart.getId();
    }
}
