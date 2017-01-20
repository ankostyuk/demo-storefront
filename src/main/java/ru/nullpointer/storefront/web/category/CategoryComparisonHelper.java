package ru.nullpointer.storefront.web.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.ModelMatch;
import ru.nullpointer.storefront.domain.support.OfferMatch;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.MatchActionInfo;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSession;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
public class CategoryComparisonHelper {

    @Resource
    private CatalogService catalogService;
    @Resource
    private MatchService matchService;
    @Resource
    private ModelService modelService;
    @Resource
    private ParamService paramService;
    @Resource
    private UIHelper uiHelper;

    public List<AbstractMatch> getComparisonMatchList(Map<Integer, List<Match>> comparisonListMap, Integer categoryId, Region region) {
        Assert.notNull(comparisonListMap);
        Assert.notNull(categoryId);

        List<Match> matchList = comparisonListMap.get(categoryId);
        if (matchList == null) {
            return Collections.emptyList();
        }
        return matchService.buildMatchResultList(matchList, region);
    }

    Category getCategory(Integer categoryId) {
        CatalogItem catalogItem = catalogService.getCategoryItemById(categoryId);
        if (catalogItem == null || !catalogItem.getActive()) {
            throw new NotFoundException();
        }

        Category category = catalogService.getCategoryById(categoryId);
        if (!category.isParametrized()) {
            return null;
        }
        return category;
    }

    List<Match> getAccessibleMatchList(List<Match> matchList, Integer categoryId) {
        Set<Match> accessibleSet = matchService.getAccessibleMatchSubset(new HashSet<Match>(matchList), categoryId, true);
        List<Match> accessibleList = new ArrayList<Match>(accessibleSet.size());
        for (Match m : matchList) {
            if (accessibleSet.contains(m)) {
                accessibleList.add(m);
            }
        }
        return accessibleList;
    }

    /**
     * Возвращает карту значений параметров моделей и предложений.
     * Ключем карты является совпадение.
     * Значением - карта значений параметров
     * @param matchList
     * @param psdId
     * @return
     */
    Map<Match, Map<Integer, Object>> getMatchParamValueMap(Map<Match, AbstractMatch> matchResultMap, Integer psdId) {
        Set<Integer> offerModelIdSet = new HashSet<Integer>();
        Set<Integer> paramSetIdSet = new HashSet<Integer>();
        for (Match m : matchResultMap.keySet()) {
            AbstractMatch matchResult = matchResultMap.get(m);
            switch (m.getType()) {
                case OFFER:
                    Offer offer = ((OfferMatch) matchResult).getOffer();
                    if (offer.isParametrized()) {
                        paramSetIdSet.add(offer.getParamSetId());
                    } else if (offer.isModelLinked()) {
                        offerModelIdSet.add(offer.getModelId());
                    }
                    break;
                case MODEL:
                    Model model = ((ModelMatch) matchResult).getModel();
                    paramSetIdSet.add(model.getParamSetId());
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип соответствия: " + m.getType());
            }
        }

        Map<Integer, Model> offerModelMap = modelService.getModelMap(offerModelIdSet);
        for (Model model : offerModelMap.values()) {
            paramSetIdSet.add(model.getParamSetId());
        }

        if (paramSetIdSet.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, Map<Integer, Object>> paramSetMap = paramService.getParamSetMap(psdId, paramSetIdSet);
        // remap
        Map<Match, Map<Integer, Object>> result = new HashMap<Match, Map<Integer, Object>>();
        for (Match m : matchResultMap.keySet()) {
            AbstractMatch matchResult = matchResultMap.get(m);
            switch (m.getType()) {
                case OFFER:
                    Offer offer = ((OfferMatch) matchResult).getOffer();
                    Match offerMatch = new Match(Match.Type.OFFER, offer.getId());
                    if (offer.isParametrized()) {
                        result.put(offerMatch, paramSetMap.get(offer.getParamSetId()));
                    } else if (offer.isModelLinked()) {
                        Model model = offerModelMap.get(offer.getModelId());
                        result.put(offerMatch, paramSetMap.get(model.getParamSetId()));
                    }
                    break;
                case MODEL:
                    Model model = ((ModelMatch) matchResult).getModel();
                    result.put(new Match(Match.Type.MODEL, model.getId()), paramSetMap.get(model.getParamSetId()));
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип соответствия: " + m.getType());
            }
        }
        return result;
    }

    String buildComparisonItemActionViewName(UserSession userSession, Integer categoryId, MatchActionInfo actionInfo, boolean inline, String noSuccessViewName, ModelMap model) {
        Assert.notNull(userSession);
        Assert.notNull(categoryId);
        Assert.notNull(actionInfo);
        if (inline) {
            model.addAttribute("_actionInfo", actionInfo);
            model.addAttribute("_categoryId", categoryId);
            model.addAttribute("_userSession", userSession);
            return "match-comparison-info";
        } else {
            if (actionInfo.isSuccess()) {
                if (StringUtils.isBlank(actionInfo.getRedirect())) {
                    return "redirect:/category/" + categoryId;
                }
                return "redirect:" + actionInfo.getRedirect();
            } else {
                model.addAttribute("_actionInfo", actionInfo);
                model.addAttribute("_categoryId", categoryId);
                model.addAttribute("_userSession", userSession);
                return noSuccessViewName;
            }
        }
    }

    String buildComparisonActionViewName(ComparisonActionInfo actionInfo, boolean inline, String noSuccessViewName, ModelMap model) {
        Assert.notNull(actionInfo);
        if (inline) {
            model.addAttribute("_actionInfo", actionInfo);
            return "comparison-action-info";
        } else {
            if (actionInfo.isSuccess()) {
                if (StringUtils.isBlank(actionInfo.getRedirect())) {
                    return "redirect:/category/" + actionInfo.getCategory().getId();
                }
                return "redirect:" + actionInfo.getRedirect();
            } else {
                model.addAttribute("_actionInfo", actionInfo);
                return noSuccessViewName;
            }
        }
    }

    void initUI(HttpServletRequest request, Integer categoryId, ModelMap model) {
        uiHelper.setCatalogTheme(catalogService.getPath(categoryId), model);
        uiHelper.initSearchSettings(request, categoryId, model);
    }
}
