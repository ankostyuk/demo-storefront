package ru.nullpointer.storefront.web.secured.company;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.domain.support.CompanyCatalogShowing;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.OfferService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/offer/move/{id}")
public class MoveOfferController {

    @Resource
    private OfferService offerService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private OfferHelper offerHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer offerId,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {

        Offer offer = offerHelper.getOffer(offerId);
        initModel(offer, cat, model);

        return "secured/company/offer/move";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer offerId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {

        Offer offer = offerHelper.getOffer(offerId);

        Category category = getCategory(categoryId);
        if (category == null) {
            model.addAttribute("categoryNotSelected", true);
            initModel(offer, cat, model);
            return "secured/company/offer/move";
        }

        if (category.getId().equals(offer.getCategoryId())) {
            model.addAttribute("categoryIsSame", true);
            initModel(offer, cat, model);
            return "secured/company/offer/move";
        }

        moveOffer(offer, categoryId);

        model.clear();

        Category oldCategory = catalogService.getCategoryById(offer.getCategoryId());
        if (!category.getUnitId().equals(oldCategory.getUnitId())) {
            // обнулить цену если другая единица измерения категории
            model.addAttribute("price", "");
        }
        model.addAttribute("redirect", redirect);
        // offer.getId() потому что id изменился
        return "redirect:/secured/company/offer/edit/" + offer.getId();
    }

    private void moveOffer(Offer offer, Integer categoryId) {
        offerService.deleteOffer(offer);
        offer.setCategoryId(categoryId);
        // отвязать от модели
        offer.setModelId(null);
        offerService.storeOffer(offer, null);
    }

    private Category getCategory(Integer categoryId) {
        if (categoryId != null) {
            return catalogService.getCategoryById(categoryId);
        }
        return null;
    }

    private void initModel(Offer offer, String cat, ModelMap model) {
        CompanyCatalogShowing catalog = AliasUtils.fromAlias(cat, CompanyCatalogShowing.MY);

        model.addAttribute("offer", offer);

        Integer categoryId = offer.getCategoryId();
        model.addAttribute("currentItem", catalogService.getItemById(categoryId));
        model.addAttribute("path", catalogService.getPath(categoryId));
        offerHelper.initCategorySelectTree(null, catalog, model);
    }
}
