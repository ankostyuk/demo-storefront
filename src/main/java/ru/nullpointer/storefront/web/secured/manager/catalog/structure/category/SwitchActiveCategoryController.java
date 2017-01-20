package ru.nullpointer.storefront.web.secured.manager.catalog.structure.category;

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
@RequestMapping("/secured/manager/catalog/structure/category/switch/active/{id}")
public class SwitchActiveCategoryController {

    private Logger logger = LoggerFactory.getLogger(SwitchActiveCategoryController.class);

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

    @InitBinder("categoryProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
            "item.active"
        });
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/structure/category/switch/active";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
                @ModelAttribute("categoryProperties") CategoryProperties categoryProperties,
                BindingResult result
            ) {
        CatalogItem categoryItem = categoryProperties.getItem();
        categoryItem.setActive(!categoryItem.getActive());
        
        catalogService.updateCategoryActive(categoryItem);
        
        return "redirect:/secured/manager/catalog/structure";
    }
}
