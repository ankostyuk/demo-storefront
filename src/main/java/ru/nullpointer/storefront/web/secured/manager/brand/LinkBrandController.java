package ru.nullpointer.storefront.web.secured.manager.brand;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.service.OfferService;

/**
 *
 * @author AlexanderYastrebov
 */
@Controller
@RequestMapping("/secured/manager/brand/link")
public class LinkBrandController {

    @Resource
    private OfferService offerService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@RequestParam(value = "name", required = false) String name, ModelMap model) {
        if (name == null || name.trim().isEmpty()) {
            return "redirect:/secured/manager/brand";
        }

        model.addAttribute("name", name);
        return "secured/manager/brand/link";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "brandId", required = false) Integer brandId) {

        if (name == null || name.trim().isEmpty() || brandId == null) {
            return "redirect:/secured/manager/brand";
        }

        offerService.setBrandByName(name, brandId);

        return "redirect:/secured/manager/brand";
    }
}
