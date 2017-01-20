package ru.nullpointer.storefront.web.secured.manager.brand;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/manager/brand/edit/{id}")
public class EditBrandController {

    @Resource
    private BrandService brandService;
    @Resource
    private ImageService imageService;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("brand")
    public Brand getBrand(@PathVariable("id") Integer brandId) {
        Brand brand = brandService.getBrandById(brandId);
        if (brand == null) {
            throw new NotFoundException();
        }
        return brand;
    }

    @InitBinder("brand")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "keywords", "site");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/brand/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("brand") Brand brand, BindingResult result,
            @RequestParam(value = "deleteLogo", required = false) Boolean deleteLogo,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            ModelMap model) {

        validator.validate(brand, result);
        if (result.hasErrors()) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(logo)) {
                model.addAttribute("reselectLogo", true);
            }
            return "secured/manager/brand/edit";
        }

        if (deleteLogo != null && deleteLogo) {
            // удалить изображение
            imageService.setBrandLogo(brand, null);
        } else {
            if (imageService.isValidImage(logo)) {
                imageService.setBrandLogo(brand, logo);
            }
        }

        brandService.updateBrandInfo(brand);

        return "redirect:/secured/manager/brand";
    }
}
