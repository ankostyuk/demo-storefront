package ru.nullpointer.storefront.web.secured.manager.catalog.structure.category;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/category/delete/{id}")
public class DeleteCategoryController {

    private Logger logger = LoggerFactory.getLogger(DeleteCategoryController.class);
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

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer categoryId,
            ModelMap model) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, true);
        model.addAttribute("categoryProperties", categoryProperties);
        return "secured/manager/catalog/structure/category/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer categoryId) throws CatalogItemException {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        catalogService.deleteCategory(categoryItem);
        return "redirect:/secured/manager/catalog/structure";
    }
}
