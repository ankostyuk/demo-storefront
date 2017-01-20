package ru.nullpointer.storefront.web.secured.company;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/offer/delete/{id}")
public class DeleteOfferController {

    private Logger logger = LoggerFactory.getLogger(DeleteOfferController.class);
    @Resource
    private OfferService offerService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ImageService imageService;

    @ModelAttribute("offer")
    public Offer getOffer(@PathVariable("id") Integer offerId) {
        Offer offer = offerService.getCompanyOfferById(offerId);
        if (offer == null) {
            logger.debug("Предложение с id: {} не найдено", offerId);
            throw new NotFoundException();
        }
        return offer;
    }

    @InitBinder("offer")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "image");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@ModelAttribute("offer") Offer offer, ModelMap model) {
        model.addAttribute("path", catalogService.getPath(offer.getCategoryId()));
        return "secured/company/offer/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("offer") Offer offer,
            @RequestParam(value = "redirect", required = false) String redirect) {

        imageService.setOfferImage(offer, null);
        offerService.deleteOffer(offer);

        if (redirect != null) {
            logger.debug("форсируем редирект на: {}", redirect);
            return "redirect:" + redirect;
        }
        return "redirect:/secured/company/offers";
    }
}
