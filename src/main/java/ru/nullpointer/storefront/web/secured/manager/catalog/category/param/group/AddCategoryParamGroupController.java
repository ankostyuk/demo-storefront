package ru.nullpointer.storefront.web.secured.manager.catalog.category.param.group;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.category.param.CategoryParamHelper;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;
import ru.nullpointer.storefront.web.ui.catalog.ParamGroupProperties;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/category/param/group/add/{id}")
public class AddCategoryParamGroupController {

    private Logger logger = LoggerFactory.getLogger(AddCategoryParamGroupController.class);

    @Resource
    private CatalogService catalogService;

    @Resource
    private ParamService paramService;

    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    @Autowired
    private CategoryParamHelper categoryParamHelper;

    @Autowired
    private BeanValidator validator;

    private CatalogItem getCategoryItem(Integer categoryId) {
        CatalogItem categoryItem = catalogService.getCategoryItemById(categoryId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с id={} не найдена", categoryId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    @ModelAttribute("paramGroupProperties")
    public ParamGroupProperties getParamGroupPropertiesModelAttribute(@PathVariable("id") Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        Category category = catalogService.getCategoryById(categoryItem.getId());
        ParamGroupProperties paramGroupProperties = categoryParamHelper.buildNewParamGroupProperties(category);
        return paramGroupProperties;
    }

    private CategoryProperties getCategoryProperties(Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, false);
        return categoryProperties;
    }

    @InitBinder("paramGroupProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{"paramGroup.name"});
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer categoryId, ModelMap model) {
        model.addAttribute("categoryProperties", getCategoryProperties(categoryId));
        return "secured/manager/catalog/category/param/group/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer categoryId, @ModelAttribute("paramGroupProperties") ParamGroupProperties paramGroupProperties, BindingResult result, ModelMap model) {
        CategoryProperties categoryProperties = getCategoryProperties(categoryId);
        model.addAttribute("categoryProperties", categoryProperties);

        validator.validate(paramGroupProperties, result);
        if (result.hasErrors()) {
            return "secured/manager/catalog/category/param/group/add";
        }

        paramService.addParamGroup(categoryProperties.getCategory(), paramGroupProperties.getParamGroup());

        // ? // first group - add param?
        StringBuilder sb = new StringBuilder("redirect:/secured/manager/catalog/category/param/")//
                .append(categoryProperties.getItem().getId());

        return sb.toString();
    }
}
