package ru.nullpointer.storefront.web.category;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.ComparisonService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.SettingsService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ActionInfo;
import ru.nullpointer.storefront.web.support.MatchActionInfo;
import ru.nullpointer.storefront.web.support.MatchParser;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class DeleteCategoryComparisonController {

    private Logger logger = LoggerFactory.getLogger(DeleteCategoryComparisonController.class);
    //
    @Resource
    private CategoryComparisonHelper comparisonHelper;
    @Resource
    private ComparisonService comparisonService;
    @Resource
    private MatchService matchService;
    @Resource
    private SettingsService settingsService;
    @Resource
    private UserSessionHelper userSessionHelper;

    @RequestMapping(value = "/category/{categoryId}/comparison/delete", method = RequestMethod.GET)
    public String handleGetDelete(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam("id") String[] idValues, ModelMap model,
            HttpServletRequest request) {

        Category category = comparisonHelper.getCategory(categoryId);
        if (category == null) {
            return "redirect:/";
        }
        model.addAttribute("category", category);

        Region region = RegionHelper.getUserRegion(settingsService.getSettings());
        model.addAttribute("comparisonMatchList", comparisonHelper.getComparisonMatchList(comparisonService.getComparisonListMap(), categoryId, region));

        List<Match> parsedList = new MatchParser().parse(idValues, ComparisonService.MAX_COMPARISON_LIST_SIZE);
        List<Match> matchList = comparisonHelper.getAccessibleMatchList(parsedList, categoryId);
        model.addAttribute("matchesToDelete", matchService.buildMatchResultList(matchList, region));

        comparisonHelper.initUI(request, categoryId, model);

        return "category/comparison/delete";
    }

    @RequestMapping(value = "/category/{categoryId}/comparison/delete", method = RequestMethod.POST)
    public String handlePostDelete(@PathVariable("categoryId") Integer categoryId,
            @RequestParam("id") String[] idValues,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "success", required = false) String successRedirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {

        Category category = comparisonHelper.getCategory(categoryId);
        if (category == null) {
            if (inline) {
                throw new NotFoundException();
            }
            return "redirect:/";
        }

        logger.debug("id values: {}", new Object[]{idValues});

        MatchParser parser = new MatchParser();
        List<Match> matchList = parser.parse(idValues, ComparisonService.MAX_COMPARISON_LIST_SIZE);

        boolean success = false;

        for (Match m : matchList) {
            success = comparisonService.deleteFromComparison(categoryId, m);
            if (!success) {
                break;
            }
        }

        if (success && StringUtils.isNotBlank(successRedirect)) {
            redirect = successRedirect;
        }

        MatchActionInfo actionInfo = new MatchActionInfo(ActionInfo.Type.MATCH_DELETE_FROM_COMPARISON, idValues);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return comparisonHelper.buildComparisonItemActionViewName(userSessionHelper.getUserSession(), categoryId, actionInfo, inline, "category/comparison/delete", model);
    }

    @RequestMapping(value = "/category/{categoryId}/comparison/clear", method = RequestMethod.GET)
    public String handleGetClear(@PathVariable("categoryId") Integer categoryId, ModelMap model, HttpServletRequest request) {

        Category category = comparisonHelper.getCategory(categoryId);
        if (category == null) {
            return "redirect:/";
        }
        model.addAttribute("category", category);

        Region region = RegionHelper.getUserRegion(settingsService.getSettings());
        model.addAttribute("comparisonMatchList", comparisonHelper.getComparisonMatchList(comparisonService.getComparisonListMap(), categoryId, region));

        comparisonHelper.initUI(request, categoryId, model);

        return "category/comparison/clear";
    }

    @RequestMapping(value = "/category/{categoryId}/comparison/clear", method = RequestMethod.POST)
    public String handlePostClear(@PathVariable("categoryId") Integer categoryId,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model) {

        Category category = comparisonHelper.getCategory(categoryId);
        if (category == null) {
            if (inline) {
                throw new NotFoundException();
            }
            return "redirect:/";
        }

        boolean success = comparisonService.deleteAllFromComparison(categoryId);

        ComparisonActionInfo actionInfo = new ComparisonActionInfo(ActionInfo.Type.COMPARISON_CLEAR, category);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return comparisonHelper.buildComparisonActionViewName(actionInfo, inline, "category/comparison/clear", model);
    }
}
