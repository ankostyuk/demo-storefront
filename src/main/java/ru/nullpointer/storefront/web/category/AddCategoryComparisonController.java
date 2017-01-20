package ru.nullpointer.storefront.web.category;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/category/{categoryId}/comparison/add")
public class AddCategoryComparisonController {

    private Logger logger = LoggerFactory.getLogger(AddCategoryComparisonController.class);
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


    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
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
        model.addAttribute("matchesToAdd", matchService.buildMatchResultList(matchList, region));

        comparisonHelper.initUI(request, categoryId, model);

        return "category/comparison/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("categoryId") Integer categoryId,
            @RequestParam("id") String[] idValues,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "inline", defaultValue = "false") Boolean inline,
            ModelMap model, HttpServletRequest request) {

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
            success = comparisonService.addToComparison(categoryId, m);
            if (!success) {
                break;
            }
        }

        MatchActionInfo actionInfo = new MatchActionInfo(ActionInfo.Type.MATCH_ADD_TO_COMPARISON, idValues);
        actionInfo.setSuccess(success);
        actionInfo.setRedirect(redirect);

        model.clear();
        return comparisonHelper.buildComparisonItemActionViewName(userSessionHelper.getUserSession(), categoryId, actionInfo, inline, "category/comparison/add", model);
    }
}
