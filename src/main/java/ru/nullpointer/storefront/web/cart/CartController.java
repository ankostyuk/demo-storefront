package ru.nullpointer.storefront.web.cart;

import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Cart;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.support.CatalogHelper;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class CartController {

    private Logger logger = LoggerFactory.getLogger(CartController.class);
    //
    @Resource
    private CartHelper cartHelper;
    @Resource
    private CatalogHelper catalogHelper;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String handleGetDefault(ModelMap model) {
        return handleGet(null, model);
    }

    @RequestMapping(value = "/cart/{cartId}", method = RequestMethod.GET)
    public String handleGet(@PathVariable("cartId") Integer cartId, ModelMap model) {
        Cart cart = cartHelper.getCart(cartId);

        model.addAttribute("cart", cart);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        Map<Match, AbstractMatch> cartMatchMap = cartHelper.getCartMatchMap(userSession.getCartItemMap().get(cart.getId()), region);
        model.addAttribute("cartMatchMap", cartMatchMap);

        model.addAttribute("categoryPathMap", catalogHelper.buildMatchCategoryPathMap(cartMatchMap.values()));

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .append(cart.getName())//
                .build(metadata);
        model.addAttribute("metadata", metadata);

        return "cart";
    }
}
