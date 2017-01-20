package ru.nullpointer.storefront.web.secured.company;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.ParamService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/offer/copy/{id}")
public class CopyOfferController {

    @Resource
    private OfferService offerService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private OfferHelper offerHelper;
    @Resource
    private ImageService imageService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer offerId,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        Offer offer = offerHelper.getOffer(offerId);

        Map<Integer, Object> paramValueMap = null;
        if (offer.isParametrized()) {
            Category category = catalogService.getCategoryById(offer.getCategoryId());
            paramValueMap = paramService.getParamSet(category.getParameterSetDescriptorId(), offer.getParamSetId());
        }

        // TODO: При таком копировании предложение всегда становится на модерацию
        
        offer.setActualDate(offerHelper.getDefaultActualDate());
        imageService.copyOfferImage(offer);
        offerService.storeOffer(offer, paramValueMap);

        Assert.isTrue(!offerId.equals(offer.getId()));

        model.clear();
        model.addAttribute("redirect", redirect);
        return "redirect:/secured/company/offer/edit/" + offer.getId();
    }
}
