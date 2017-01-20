package ru.nullpointer.storefront.web.secured.company.settings;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.support.RegionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/settings")
public class CompanySettingsController {

    private Logger logger = LoggerFactory.getLogger(CompanySettingsController.class);
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private CompanyService companyService;
    @Resource
    private ImageService imageService;
    @Resource
    private RegionHelper regionHelper;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("company")
    public Company getCompany() {
        return securityService.getAuthenticatedCompany();
    }

    @InitBinder("company")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "name",
                "address",
                "contactPhone", "contactPerson",
                "site",
                "schedule", "scope",
                "paymentConditions.text",
                "paymentConditions.cash",
                "paymentConditions.cashless");
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(@ModelAttribute("company") Company company, ModelMap model) {
        model.addAttribute("companyRegion", regionHelper.getRegionWitnPath(company.getRegionId()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("company") Company company, Errors errors,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "deleteLogo", required = false) Boolean deleteLogo,
            ModelMap model,
            HttpServletRequest request) {

        Region region = regionHelper.initRegion(request, model);
        company.setRegionId(region == null ? null : region.getId());

        logger.debug("Company: {}", company);
        validator.validate(company, errors);

        if (errors.hasErrors()) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(logo)) {
                model.addAttribute("reselectLogo", true);
            }
            return "secured/company/settings";
        }

        if (deleteLogo != null && deleteLogo) {
            // удалить изображение
            imageService.setCompanyLogo(company, null);
        } else {
            if (imageService.isValidImage(logo)) {
                imageService.setCompanyLogo(company, logo);
            }
        }

        companyService.updateCompanyInfo(company);

        model.clear();
        return "redirect:/secured/company/settings?updated";
    }
}
