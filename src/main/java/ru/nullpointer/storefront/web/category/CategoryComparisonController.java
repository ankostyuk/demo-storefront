package ru.nullpointer.storefront.web.category;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.OfferMatch;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ComparisonService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.support.MatchParser;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class CategoryComparisonController {

    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;
    @Resource
    private ParamService paramService;
    @Resource
    private MatchService matchService;
    @Resource
    private UserSessionHelper userSessionHelper;
    //
    @Resource
    private CategoryComparisonHelper comparisonHelper;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/category/{id}/comparison", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer categoryId,
            @RequestParam(value = "ids", required = false) String idString,
            @RequestParam(value = "filter", defaultValue = "all") String filterName,
            ModelMap model, HttpServletRequest request) {
        
        CatalogItem catalogItem = catalogService.getCategoryItemById(categoryId);
        if (catalogItem == null || !catalogItem.getActive()) {
            return "redirect:/";
        }

        Category category = catalogService.getCategoryById(categoryId);
        if (!category.isParametrized()) {
            return "redirect:/category/" + categoryId;
        }

        List<Match> parsedList = new MatchParser().parse(idString, ComparisonService.MAX_COMPARISON_LIST_SIZE);
        List<Match> matchList = comparisonHelper.getAccessibleMatchList(parsedList, categoryId);

        /*
        if (matchList.size() <= 1) {
        return "redirect:/category/" + categoryId;
        }
         */

        model.addAttribute("matchList", matchList);

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        model.addAttribute("paramModel", paramModel);

        initModel(catalogItem, category, model);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        model.addAttribute("regionAware", region != null);
        
        Map<Match, AbstractMatch> matchResultMap = matchService.buildMatchResultMap(matchList, region);
        model.addAttribute("matchResultMap", matchResultMap);

        Map<Match, Map<Integer, Object>> matchParamValueMap = comparisonHelper.getMatchParamValueMap(matchResultMap, category.getParameterSetDescriptorId());
        model.addAttribute("matchParamValueMap", matchParamValueMap);

        initFilterModel(filterName, paramModel, matchList, matchResultMap, matchParamValueMap, model);

        uiHelper.initSearchSettings(request, categoryId, model);

        return "category/comparison";
    }

    public enum Filter {
        all, different;
    }

    private void initFilterModel(String filterName, ParamModel paramModel, List<Match> matchList, Map<Match, AbstractMatch> matchResultMap, Map<Match, Map<Integer, Object>> matchParamValueMap, ModelMap model) {
        Map<Integer, Set<Filter>> filterParamMap = new HashMap<Integer, Set<Filter>>();
        Map<Integer, Set<Filter>> filterParamGroupMap = new HashMap<Integer, Set<Filter>>();

        List<Param> paramList = paramModel.getParamList();
        Integer prevGroupId = null;
        Set<Filter> paramGroupFilterSet = new HashSet<Filter>();

        int last = paramList.size() - 1;

        for (int i = 0; i <= last; i++) {
            Param param = paramList.get(i);

            // Для групп параметров
            if (!param.getParamGroupId().equals(prevGroupId)) {
                if (prevGroupId != null) {
                    filterParamGroupMap.put(prevGroupId, new HashSet<Filter>(paramGroupFilterSet));
                }
                paramGroupFilterSet.clear();
                prevGroupId = param.getParamGroupId();
            }

            Set<Filter> paramFilterSet = new HashSet<Filter>();

            Object prevParamValue = null;
            int diffParamValueCount = 0;
            
            for (Match match : matchList) {
                AbstractMatch matchResult = matchResultMap.get(match);

                Object paramValue = null;
                switch (matchResult.getType()) {
                    case OFFER:
                        Offer offer = ((OfferMatch)matchResult).getOffer();
                        if (offer.isParametrized() || offer.isModelLinked()) {
                            paramValue = matchParamValueMap.get(match).get(param.getId());
                        }
                        break;
                    case MODEL:
                        paramValue = matchParamValueMap.get(match).get(param.getId());
                        break;
                }

                if (paramValue != null) {
                    // В Filter.all (и в остальные фильтры) попадает параметр, если хотя бы одно значение ненулевое
                    paramFilterSet.add(Filter.all);

                    // Для Filter.different
                    if (!paramValue.equals(prevParamValue)) {
                        diffParamValueCount++;
                        prevParamValue = paramValue;
                    }
                }
            }

            if (diffParamValueCount > 1) {
                paramFilterSet.add(Filter.different);
            }

            filterParamMap.put(param.getId(), new HashSet<Filter>(paramFilterSet));

            paramGroupFilterSet.addAll(paramFilterSet);

            // Для последней группы параметров
            if (i == last) {
                if (prevGroupId != null) {
                    filterParamGroupMap.put(prevGroupId, new HashSet<Filter>(paramGroupFilterSet));
                }
            }
        }

        model.addAttribute("filterParamMap", filterParamMap);
        model.addAttribute("filterParamGroupMap", filterParamGroupMap);
        model.addAttribute("filterName", filterName);
    }

    private void initModel(CatalogItem catalogItem, Category category, ModelMap model) {
        model.addAttribute("catalogItem", catalogItem);
        model.addAttribute("category", category);

        List<CatalogItem> path = catalogService.getPath(category.getId());
        model.addAttribute("path", path);

        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .append(catalogItem.getName())//
                .appendMessage("ui.category.comparison.title")//
                .build(metadata);
        model.addAttribute("metadata", metadata);

        uiHelper.setCatalogTheme(path, model);
    }
}
