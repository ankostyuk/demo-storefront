package ru.nullpointer.storefront.web;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.support.UIHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(ModelMap model, HttpServletRequest request) {
        List<CatalogItem> itemList = catalogService.getActiveChildrenList(null);

        Set<Integer> sectionIdSet = getSectionIdSet(itemList);
        Map<Integer, List<CatalogItem>> popularMap = catalogService.getPopularCategoriesMap(sectionIdSet);

        Set<Integer> categoryIdSet = getCategoryIdSet(itemList, popularMap);
        Map<Integer, Category> categoryMap = catalogService.getCategoryMap(categoryIdSet); // TODO зачем? Вроде в JSP не используется - проверить

        logger.debug("categoryIdSet: {}", categoryIdSet);
        logger.debug("categoryMap: {}", categoryMap);

        model.addAttribute("itemList", itemList);
        model.addAttribute("popularMap", popularMap);
        model.addAttribute("categoryMap", categoryMap); // TODO зачем? Вроде в JSP не используется - проверить

        uiHelper.initSearchSettings(request, null, model);

        return "index";
    }

    private Set<Integer> getSectionIdSet(List<CatalogItem> itemList) {
        Set<Integer> sectionIdSet = new HashSet<Integer>();
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.SECTION) {
                sectionIdSet.add(item.getId());
            }
        }
        return sectionIdSet;
    }

    private Set<Integer> getCategoryIdSet(List<CatalogItem> itemList, Map<Integer, List<CatalogItem>> popularMap) {
        Set<Integer> categoryIdSet = new HashSet<Integer>();
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryIdSet.add(item.getId());
            } else {
                List<CatalogItem> popular = popularMap.get(item.getId());
                if (popular != null) {
                    for (CatalogItem p : popular) {
                        categoryIdSet.add(p.getId());
                    }
                }
            }
        }
        return categoryIdSet;
    }
}
