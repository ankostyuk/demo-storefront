package ru.nullpointer.storefront.web.secured.manager.catalog.structure.category;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/category/edit/section/{id}")
public class EditParentCategoryController {

    private Logger logger = LoggerFactory.getLogger(EditParentCategoryController.class);
    @Resource
    private CatalogService catalogService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    private CatalogItem getCategoryItem(Integer categoryId) {
        CatalogItem categoryItem = catalogService.getCategoryItemById(categoryId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с id={} не найдена", categoryId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    @ModelAttribute("categoryProperties")
    public CategoryProperties getCategoryProperties(@PathVariable("id") Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, true);
        return categoryProperties;
    }

    @ModelAttribute("parentItemList")
    public List<ItemProperties> getParentItemList(@PathVariable("id") Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);

        List<ItemProperties> parentItemList = new ArrayList<ItemProperties>();

        // no catalog root for category

        List<CatalogItem> subTree = catalogService.getSubTree(null);
        for (CatalogItem item : subTree) {
            ItemProperties parentItemProperties = catalogStructureHelper.buildItemProperties(item, false);

            if (catalogService.isSectionItem(item) && !catalogService.isEquals(item, categoryItem) && !catalogService.isChildItem(item, categoryItem)) {
                //
                parentItemProperties.setCanChoose(true);
            } else {
                parentItemProperties.setCanChoose(false);
            }
            parentItemProperties.setLevel(catalogService.getItemLevel(item));
            
            parentItemList.add(parentItemProperties);
        }

        return parentItemList;
    }

    @InitBinder("categoryProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
                    "parentItemId"
                });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/structure/category/edit/section";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("categoryProperties") CategoryProperties categoryProperties,
            BindingResult result) throws CatalogItemException {

        catalogService.changeParentItem(categoryProperties.getItem(), categoryProperties.getParentItemId());

        return "redirect:/secured/manager/catalog/structure";
    }
}
