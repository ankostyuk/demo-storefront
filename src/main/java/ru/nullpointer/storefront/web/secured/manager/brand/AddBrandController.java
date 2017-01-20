package ru.nullpointer.storefront.web.secured.manager.brand;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class AddBrandController {

    @Resource
    private BrandService brandService;
    @Resource
    private ImageService imageService;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("brand")
    public Brand getBrand() {
        Brand brand = new Brand();
        return brand;
    }

    @InitBinder("brand")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "keywords", "site");
    }

    @RequestMapping(value = "/secured/manager/brand/add", method = RequestMethod.GET)
    public void handleGet(@ModelAttribute("brand") Brand brand) {
        // @ModelAttribute("brand") для биндинга параметров
    }

    @RequestMapping(value = "/secured/manager/brand/add", method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("brand") Brand brand, BindingResult result,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            ModelMap model) {

        validator.validate(brand, result);
        if (result.hasErrors()) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(logo)) {
                model.addAttribute("reselectLogo", true);
            }
            return "secured/manager/brand/add";
        }

        if (imageService.isValidImage(logo)) {
            imageService.setBrandLogo(brand, logo);
        }

        brandService.storeBrand(brand);

        return "redirect:/secured/manager/brand";
    }
}
