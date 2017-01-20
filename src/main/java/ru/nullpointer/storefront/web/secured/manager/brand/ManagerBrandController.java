package ru.nullpointer.storefront.web.secured.manager.brand;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.support.ResultGroup;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.OfferService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ManagerBrandController {

    @Resource
    private BrandService brandService;
    @Resource
    private OfferService offerService;

    @RequestMapping(value = "/secured/manager/brand", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {

        model.addAttribute("unlinkedBrandNames", offerService.getUnlinkedBrandNameList());
        
        List<String> brandToc = brandService.getBrandToc();
        if (brandToc.isEmpty()) {
            return;
        }

        List<ResultGroup<Brand>> brandGroupList = brandService.getBrandGroupList();

        model.addAttribute("brandGroupList", brandGroupList);
        model.addAttribute("brandToc", brandToc);
    }
}
