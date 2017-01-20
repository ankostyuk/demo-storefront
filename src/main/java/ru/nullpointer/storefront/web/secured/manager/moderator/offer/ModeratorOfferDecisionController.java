package ru.nullpointer.storefront.web.secured.manager.moderator.offer;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/manager/moderator/offer/decision/{id}")
public class ModeratorOfferDecisionController {

    private Logger logger = LoggerFactory.getLogger(ModeratorOfferDecisionController.class);
    //
    @Resource
    private OfferService offerService;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer id,
            @RequestParam(value = "action", required = false) String action,
            ModelMap model) {

        initModel(id, action, model);

        return "secured/manager/moderator/offer/decision";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer id,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "rejectionValue", required = false) List<Offer.Rejection> rejectionValues,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model) {

        initModel(id, action, model);

        logger.debug("action: {}", action);
        
        if ("approve".equals(action)) {
            logger.debug("Approving offer {}", id);
            
            offerService.approveOffer(id);
        } else if ("reject".equals(action)) {
            if (rejectionValues != null && !rejectionValues.isEmpty()) {
                offerService.rejectOffer(id, rejectionValues);
            } else {
                model.addAttribute("selectRejection", true);
                return "secured/manager/moderator/offer/decision";
            }
        }

        // очистить модель чтобы объекты модели не превратились в параметры URL
        model.clear();
        
        if (redirect != null) {
            logger.debug("форсируем редирект на: {}", redirect);
            return "redirect:" + redirect;
        }
        return "redirect:/secured/manager/moderator/offer";
    }
 
    private void initModel(Integer offerId, String action, ModelMap model) {
        Offer offer = offerService.getModeratorOfferById(offerId);
        if (offer == null) {
            throw new NotFoundException();
        }
        model.addAttribute("offer", offer);
        model.addAttribute("action", action);
        model.addAttribute("rejectionValues", Offer.Rejection.values());
    }
}
