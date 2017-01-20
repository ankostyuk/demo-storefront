package ru.nullpointer.storefront.web.brand;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.support.CatalogHelper;
import ru.nullpointer.storefront.web.support.CatalogHelper.RootSectionRetriever;
import ru.nullpointer.storefront.web.support.UIHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class BrandController {

    private static final int FIND_BRAND_LIMIT = 5;

    @Resource
    private BrandService brandService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private UIHelper uiHelper;
    @Resource
    private CatalogHelper catalogHelper;

    public Brand getBrand(Integer brandId) {
        Brand brand = brandService.getBrandById(brandId);
        if (brand == null) {
            throw new NotFoundException("Бренд с id  «" + brandId + "» не найден");
        }
        return brand;
    }

    @ResponseBody
    @RequestMapping(value = "/brand/suggest", method = RequestMethod.GET)
    public List<Brand> handleSuggest(@RequestParam("q") String query) {
        return brandService.findBrandListByText(query, FIND_BRAND_LIMIT);
    }

    @RequestMapping(value = "/brand")
    public String handleGet() {
        // TODO all brands
        return "redirect:/";
    }

    @RequestMapping(value = "/brand/{id}", method = RequestMethod.GET)
    public String handleBrandGet(@PathVariable("id") Integer brandId, ModelMap model, HttpServletRequest request) {

        Brand brand = getBrand(brandId);

        model.addAttribute("brand", brand);

        buildCategoryList(brand, model);

        Metadata metadata = new Metadata();
        metadata.setTitle(brand.getName());

        model.addAttribute("metadata", metadata);

        uiHelper.initSearchSettings(request, null, model);

        return "brand";
    }

    private void buildCategoryList(Brand brand, ModelMap model) {
        List<CatalogItem> brandCategoryList = catalogService.getBrandActiveCategoryList(brand.getId());
        RootSectionRetriever rootSectionRetriever = catalogHelper.buildRootSectionRetrieverByCategoryList(brandCategoryList);
        model.addAttribute("rootSectionList", rootSectionRetriever.getRootSectionList());
        model.addAttribute("rootSectionCategoryMap", rootSectionRetriever.getRootSectionCategoryMap());
    }
}
