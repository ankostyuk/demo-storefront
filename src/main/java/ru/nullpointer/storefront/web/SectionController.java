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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.support.UIHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class SectionController {

    private Logger logger = LoggerFactory.getLogger(SectionController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/section")
    public String handleGet() {
        // возвращаем на главную
        return "redirect:/";
    }

    @RequestMapping(value = "/section/{id}")
    public String handleSectionGet(@PathVariable("id") Integer sectionId, ModelMap model, HttpServletRequest request) {
        CatalogItem section = catalogService.getSectionItemById(sectionId);
        if (section == null || !section.getActive()) {
            // возвращаем на главную
            return "redirect:/";
        }

        List<CatalogItem> itemList = catalogService.getActiveChildrenList(sectionId);

        Set<Integer> sectionIdSet = getSectionIdSet(itemList);
        Map<Integer, List<CatalogItem>> popularMap = catalogService.getPopularCategoriesMap(sectionIdSet);

        Set<Integer> categoryIdSet = getCategoryIdSet(itemList, popularMap);
        Map<Integer, Category> categoryMap = catalogService.getCategoryMap(categoryIdSet);

        Metadata metadata = getMetadata(section, itemList, popularMap);

        logger.debug("categoryIdSet: {}", categoryIdSet);
        logger.debug("categoryMap: {}", categoryMap);

        List<CatalogItem> path = catalogService.getPath(section.getId());
        model.addAttribute("path", path);
        model.addAttribute("section", section);
        model.addAttribute("itemList", itemList);
        model.addAttribute("popularMap", popularMap);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("metadata", metadata);

        uiHelper.initSearchSettings(request, section.getId(), model);
        uiHelper.setCatalogTheme(path, model);

        return "section";
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

    private Metadata getMetadata(CatalogItem section, List<CatalogItem> itemList, Map<Integer, List<CatalogItem>> popularMap) {
        Metadata metadata = new Metadata();
        metadata.setTitle(section.getName());

        for (CatalogItem item : itemList) {
            // добавить в ключевые слова
            metadata.addKeyword(item.getName());

            // добавить в ключевые слова популярные категории
            List<CatalogItem> popular = popularMap.get(item.getId());
            if (popular != null) {
                for (CatalogItem p : popular) {
                    metadata.addKeyword(p.getName());
                }
            }
        }
        return metadata;
    }
}
