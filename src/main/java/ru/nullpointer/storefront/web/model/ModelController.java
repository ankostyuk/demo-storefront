package ru.nullpointer.storefront.web.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.OfferMatch;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.MatchFilterParser;
import ru.nullpointer.storefront.web.support.ParamInputHelper;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.ui.ParamInput;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class ModelController {

    private static final int FIND_MODEL_LIMIT = 5;
    @Resource
    private ModelService modelService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private BrandService brandService;
    @Resource
    private MatchService matchService;
    @Resource
    private OfferService offerService;
    @Resource
    private UnitService unitService;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private UIHelper uiHelper;

    @ResponseBody
    @RequestMapping(value = "/model/suggest", method = RequestMethod.GET)
    public List<Model> handleSuggest(
            @RequestParam("q") String query,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "brandId", required = false) Integer brandId) {
        return modelService.findModelListByText(query, categoryId, brandId, FIND_MODEL_LIMIT);
    }

    @RequestMapping(value = "/model")
    public String handleGet() {
        return "redirect:/";
    }

    @RequestMapping(value = "/model/{id}", method = RequestMethod.GET)
    public String handleModelGet(
            @PathVariable("id") Integer modelId,
            ModelMap modelMap, HttpServletRequest request) {

        Model model = getModel(modelId);

        modelMap.addAttribute("model", model);

        UserSession userSession = userSessionHelper.getUserSession();
        modelMap.addAttribute("userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        modelMap.addAttribute("regionAware", region != null);

        List<CatalogItem> categoryPath = catalogService.getPath(model.getCategoryId());
        CatalogItem categoryItem = categoryPath.get(categoryPath.size() - 1);
        modelMap.addAttribute("modelCategoryPath", categoryPath);

        Category category = catalogService.getCategoryById(model.getCategoryId());
        modelMap.addAttribute("unit", unitService.getUnitById(category.getUnitId()));
        
        CatalogItem catalogItem = catalogService.getCategoryItemById(category.getId());
        modelMap.addAttribute("catalogItem", catalogItem);

        initParam(model.getParamSetId(), category, modelMap);

        ModelInfo modelInfo = matchService.getModelInfo(modelId, region);
        modelMap.addAttribute("modelInfo", modelInfo);
        
        Brand brand = brandService.getBrandById(model.getBrandId());
        modelMap.addAttribute("brand", brand);

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .append(categoryItem.getName())//
                .append(model.getName())//
                .build(metadata);
        modelMap.addAttribute("metadata", metadata);

        uiHelper.initSearchSettings(request, categoryItem.getId(), modelMap);
        uiHelper.setCatalogTheme(categoryPath, modelMap);

        return "model";
    }

    @RequestMapping(value = "/model/{id}/offers", method = RequestMethod.GET)
    public String handleModelOffersGet(
            @PathVariable("id") Integer modelId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            ModelMap modelMap, HttpServletRequest request) {

        Model model = getModel(modelId);

        modelMap.addAttribute("model", model);

        UserSession userSession = userSessionHelper.getUserSession();
        modelMap.addAttribute("userSession", userSession);

        MatchSorting sorting = AliasUtils.fromAlias(sort, MatchSorting.PRICE_ASCENDING);
        modelMap.addAttribute("sorting", sorting);

        PageConfig pageConfig = new PageConfig(page, userSession.getSettings().getPageSize());

        List<CatalogItem> categoryPath = catalogService.getPath(model.getCategoryId());
        CatalogItem categoryItem = categoryPath.get(categoryPath.size() - 1);
        modelMap.addAttribute("modelCategoryPath", categoryPath);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        modelMap.addAttribute("regionAware", region != null);

        ModelInfo modelInfo = matchService.getModelInfo(modelId, region);
        modelMap.addAttribute("modelInfo", modelInfo);

        Category category = catalogService.getCategoryById(model.getCategoryId());
        modelMap.addAttribute("unit", unitService.getUnitById(category.getUnitId()));
        
        CatalogItem catalogItem = catalogService.getCategoryItemById(category.getId());
        modelMap.addAttribute("catalogItem", catalogItem);

        MatchFilterParser parser = new MatchFilterParser(Collections.<Brand>emptyList(), null);

        MatchFilter matchFilter = parser.parse(request);
        modelMap.addAttribute("matchFilter", matchFilter);

        String filterUrlParams = parser.serialize(matchFilter);
        modelMap.addAttribute("filterUrlParams", filterUrlParams);

        modelMap.addAttribute("priceInterval", offerService.getModelOfferPriceInterval(modelId));

        PaginatedQueryResult<OfferMatch> queryResult = matchService.getModelOfferMatchList(model.getId(), pageConfig, sorting, region, matchFilter);
        modelMap.addAttribute("queryResult", queryResult);

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .appendMessage("ui.model.offers.title")//
                .append(categoryItem.getName())//
                .append(model.getName())//
                .build(metadata);
        modelMap.addAttribute("metadata", metadata);

        uiHelper.initSearchSettings(request, categoryItem.getId(), modelMap);
        uiHelper.setCatalogTheme(categoryPath, modelMap);

        return "model/offers";
    }

    @ResponseBody
    @RequestMapping(value = "/model/{id}/offers/prefilter", method = RequestMethod.GET)
    public int handleModelOffersPrefilterGet(
            @PathVariable("id") Integer modelId,
            HttpServletRequest request) {

        Model model = getModel(modelId);

        UserSession userSession = userSessionHelper.getUserSession();
        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        
        MatchFilterParser parser = new MatchFilterParser(Collections.<Brand>emptyList(), null);
        MatchFilter matchFilter = parser.parse(request);

        return matchService.getModelOfferMatchCount(model.getId(), region, matchFilter);
    }

    private void initParam(Integer paramSetId, Category category, ModelMap modelMap) {
        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);

        ParamInput paramInput = new ParamInput(paramService.getParamSet(category.getParameterSetDescriptorId(), paramSetId));
        modelMap.addAttribute("paramInput", paramInput);

        modelMap.addAttribute("paramCountGroupMap", ParamInputHelper.buildParamCountGroupMap(paramModel, paramInput));
    }

    private Model getModel(Integer modelId) {
        Model model = modelService.getModelById(modelId);
        if (model == null) {
            throw new NotFoundException("Модель с id  «" + modelId + "» не найдена");
        }
        return model;
    }
}
