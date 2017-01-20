package ru.nullpointer.storefront.web.secured.company;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.domain.support.CompanyCatalogShowing;
import ru.nullpointer.storefront.service.CatalogService;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/offer/category")
public class SelectOfferCategoryController {

    @Resource
    private OfferHelper offerHelper;
    @Resource
    private CatalogService catalogService;

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {

        initModel(id, cat, model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {

        if (!isCategoryIdValid(categoryId)) {
            model.addAttribute("categoryNotSelected", true);
            initModel(id, cat, model);
            return "secured/company/offer/category";
        }
        
        return "redirect:/secured/company/offer/add?categoryId=" + categoryId;
    }

    private boolean isCategoryIdValid(Integer categoryId) {
        if (categoryId == null) {
            return false;
        }

        Category category = catalogService.getCategoryById(categoryId);
        return category != null;
    }

    private void initModel(Integer id, String cat, ModelMap model) {
        CompanyCatalogShowing catalog = AliasUtils.fromAlias(cat, CompanyCatalogShowing.MY);

        offerHelper.initCategorySelectTree(id, catalog, model);
    }
}
