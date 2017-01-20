package ru.nullpointer.storefront.web.secured.company.settings;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.RegionService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/settings/delivery/region/delete/{id}")
public class DeleteCompanyDeliveryRegionController {

    private Logger logger = LoggerFactory.getLogger(DeleteCompanyDeliveryRegionController.class);
    //
    @Resource
    private CompanyService companyService;
    @Resource
    private RegionService regionService;

    private Region getDeliveryRegion(Integer deliveryRegionId) {
        List<Region> deliveryRegionList = companyService.getCompanyDeliveryRegionList();
        Region deliveryRegion = null;
        for (Region r : deliveryRegionList) {
            if (r.getId().equals(deliveryRegionId)) {
                deliveryRegion = r;
                break;
            }
        }
        if (deliveryRegion == null) {
            throw new NotFoundException();
        }
        return deliveryRegion;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer deliveryRegionId, ModelMap model) {
        Region deliveryRegion = getDeliveryRegion(deliveryRegionId);
        deliveryRegion.setPath(regionService.getRegionPath(deliveryRegion));
        model.addAttribute("deliveryRegion", deliveryRegion);
        
        return "secured/company/settings/delivery/region/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer deliveryRegionId, ModelMap model) {
        Region deliveryRegion = getDeliveryRegion(deliveryRegionId);

        companyService.deleteCompanyDeliveryRegion(deliveryRegion);
        
        return "redirect:/secured/company/settings/delivery?updated";
    }
}