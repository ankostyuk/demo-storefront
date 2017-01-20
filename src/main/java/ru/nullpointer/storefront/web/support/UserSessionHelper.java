package ru.nullpointer.storefront.web.support;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.service.CartService;
import ru.nullpointer.storefront.service.ComparisonService;
import ru.nullpointer.storefront.service.SettingsService;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class UserSessionHelper {

    @Resource
    private SettingsService settingsService;
    @Resource
    private ComparisonService comparisonService;
    @Resource
    private CartService cartService;

    public UserSession getUserSession() {
        return new UserSession(
                settingsService.getSettings(),
                comparisonService.getComparisonListMap(),
                cartService.getCartList(),
                cartService.getCartItemMap());
    }
}
