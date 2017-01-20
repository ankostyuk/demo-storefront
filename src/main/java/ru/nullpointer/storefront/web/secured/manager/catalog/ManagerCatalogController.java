package ru.nullpointer.storefront.web.secured.manager.catalog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ManagerCatalogController {

    @RequestMapping("/secured/manager/catalog")
    public void handleGet() {
    }
}
