package ru.nullpointer.storefront.web;

import ru.nullpointer.storefront.web.support.MatchFilterParser;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.category.CategoryComparisonHelper;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class CategoryController {

    private Logger logger = LoggerFactory.getLogger(CategoryController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private MatchService matchService;
    @Resource
    private UnitService unitService;
    @Resource
    private ParamService paramService;
    @Resource
    private BrandService brandService;
    @Resource
    private OfferService offerService;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private CategoryComparisonHelper categoryComparisonHelper;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/category")
    public String handleGet() {
        // возвращаем на главную
        return "redirect:/";
    }

    @RequestMapping(value = "/category/{id}")
    public String handleCategoryGet(
            @PathVariable("id") Integer categoryItemId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            ModelMap model, HttpServletRequest request) {

        CatalogItem catalogItem = catalogService.getCategoryItemById(categoryItemId);
        if (catalogItem == null || !catalogItem.getActive()) {
            return "redirect:/";
        }

        Category category = catalogService.getCategoryById(categoryItemId);

        initModel(catalogItem, category, model);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        model.addAttribute("regionAware", region != null);

        List<AbstractMatch> comparisonList = categoryComparisonHelper.getComparisonMatchList(userSession.getComparisonMap(), categoryItemId, region);
        model.addAttribute("comparisonList", comparisonList);

        MatchSorting sorting = AliasUtils.fromAlias(sort, MatchSorting.PRICE_ASCENDING);
        model.addAttribute("sorting", sorting);

        PageConfig pageConfig = new PageConfig(page, userSession.getSettings().getPageSize());

        List<Brand> brandList = brandService.getCategoryBrandList(category.getId());
        model.addAttribute("brandList", brandList);

        model.addAttribute("priceInterval", offerService.getCategoryOfferPriceInterval(category.getId()));

        ParamModel paramModel = null;
        if (category.isParametrized()) {
            paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
            model.addAttribute("paramModel", paramModel);
        }

        MatchFilterParser parser = new MatchFilterParser(brandList, paramModel);

        MatchFilter matchFilter = parser.parse(request);
        model.addAttribute("matchFilter", matchFilter);

        logger.debug("matchFilter: {}", matchFilter);

        String filterUrlParams = parser.serialize(matchFilter);
        model.addAttribute("filterUrlParams", filterUrlParams);

        PaginatedQueryResult<AbstractMatch> queryResult = matchService.getCategoryMatchList(category.getId(), pageConfig, sorting, region, matchFilter);
        if (queryResult.getList().isEmpty()) {
            // пустой список, предпринять действия - например перенаправить на спец страницу
        }

        model.addAttribute("queryResult", queryResult);

        uiHelper.initSearchSettings(request, catalogItem.getId(), model);

        return "category";
    }

    @RequestMapping(value = "/category/{id}/comparison/list")
    public String handleCategoryComparisonListGet(
            @PathVariable("id") Integer categoryItemId,
            ModelMap model) {

        model.addAttribute("_categoryId", categoryItemId);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("_userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        List<AbstractMatch> comparisonList = categoryComparisonHelper.getComparisonMatchList(userSession.getComparisonMap(), categoryItemId, region);
        model.addAttribute("_comparisonList", comparisonList);

        return "category-comparison-list";
    }

    @ResponseBody
    @RequestMapping(value = "/category/{id}/prefilter")
    public int handleCategoryPrefilterGet(
            @PathVariable("id") Integer categoryItemId,
            HttpServletRequest request) {

        Category category = catalogService.getCategoryById(categoryItemId);

        UserSession userSession = userSessionHelper.getUserSession();
        Region region = RegionHelper.getUserRegion(userSession.getSettings());

        List<Brand> brandList = brandService.getCategoryBrandList(category.getId());

        ParamModel paramModel = null;
        if (category.isParametrized()) {
            paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        }

        MatchFilterParser parser = new MatchFilterParser(brandList, paramModel);
        MatchFilter matchFilter = parser.parse(request);

        return matchService.getCategoryMatchCount(categoryItemId, region, matchFilter);
    }

    private void initModel(CatalogItem catalogItem, Category category, ModelMap model) {
        model.addAttribute("catalogItem", catalogItem);
        model.addAttribute("category", category);

        List<CatalogItem> path = catalogService.getPath(category.getId());
        model.addAttribute("path", path);

        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));

        Metadata metadata = new Metadata();
        metadata.setTitle(catalogItem.getName());

        model.addAttribute("metadata", metadata);

        uiHelper.setCatalogTheme(path, model);
    }
}
