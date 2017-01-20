package ru.nullpointer.storefront.web.secured.company.settings;

import java.util.ArrayList;
import java.util.List;
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
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.RegionService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.support.RegionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/settings/delivery")
public class CompanyDeliverySettingsController {

    private Logger logger = LoggerFactory.getLogger(CompanyDeliverySettingsController.class);
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private CompanyService companyService;
    @Resource
    private RegionService regionService;
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
        binder.setAllowedFields("deliveryConditions");
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(
            @ModelAttribute("company") Company company,
            ModelMap model) {
        initDeliveryRegionModel(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("company") Company company, Errors errors,
            @RequestParam(value = "deliveryRegionId", required = false) List<Integer> deliveryRegionIdList,
            @RequestParam(value = "deliveryConditions", required = false) String deliveryConditions,
            ModelMap model,
            HttpServletRequest request) {

        if (deliveryRegionIdList == null) {
            deliveryRegionIdList = new ArrayList<Integer>();
        }

        Region region = regionHelper.initRegion(request, model);
        if (region != null) {
            deliveryRegionIdList.add(region.getId());
            model.remove("initRegionText");
            model.remove("initRegion");
        }

        companyService.setCompanyDeliveryRegionList(deliveryRegionIdList);

        validator.validate(company, errors);
        if (errors.hasErrors() || model.containsAttribute("initRegionError")) {
            initDeliveryRegionModel(model);
            return "secured/company/settings/delivery";
        }

        if (deliveryConditions != null) {
            company.setDeliveryConditions(deliveryConditions);
            companyService.updateCompanyInfo(company);
        }

        model.clear();
        return "redirect:/secured/company/settings/delivery?updated";
    }

    private void initDeliveryRegionModel(ModelMap model) {
        List<Region> deliveryRegionList = companyService.getCompanyDeliveryRegionList();
        for (Region region : deliveryRegionList) {
            region.setPath(regionService.getRegionPath(region));
        }
        model.addAttribute("deliveryRegionList", deliveryRegionList);
    }
}
