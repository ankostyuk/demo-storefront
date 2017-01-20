package ru.nullpointer.storefront.web.secured.company;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class CompanyController {

    @RequestMapping("/secured/company")
    public String handleIndexPage(@RequestParam(value = "first_login", required = false) Boolean firstLogin) {
        if (firstLogin != null && firstLogin) {
            return "redirect:/secured/company/welcome";
        }
        return "redirect:/secured/company/offers";
    }
}
