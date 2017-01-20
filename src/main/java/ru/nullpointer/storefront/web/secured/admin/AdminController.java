package ru.nullpointer.storefront.web.secured.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class AdminController {

    private Logger logger = LoggerFactory.getLogger(AdminController.class);

    @RequestMapping(value = "/secured/admin", method = RequestMethod.GET)
    public void handleGet() {
        logger.debug("Admin backend");
    }
}
