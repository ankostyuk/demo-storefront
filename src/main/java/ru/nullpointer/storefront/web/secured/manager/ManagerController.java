package ru.nullpointer.storefront.web.secured.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ManagerController {

    private Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @RequestMapping("/secured/manager")
    public void handle() {
        logger.debug("Manager backend");
    }
}
