package ru.nullpointer.storefront.web.secured.manager.catalog.structure.category;

import java.util.ArrayList;
import java.util.List;
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
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.SelectOption;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/category/edit/{id}")
public class EditCategoryController {

    private Logger logger = LoggerFactory.getLogger(EditCategoryController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;
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

    @ModelAttribute("categoryProperties")
    public CategoryProperties getCategoryProperties(@PathVariable("id") Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, true);
        return categoryProperties;
    }

    @ModelAttribute("unitSelectList")
    public List<SelectOption<Integer>> getUnitSelectList() {
        List<SelectOption<Integer>> unitSelectList = new ArrayList<SelectOption<Integer>>();

        List<Unit> allUnits = unitService.getAllUnits();
        for (Unit unit : allUnits) {
            unitSelectList.add(new SelectOption<Integer>(unit.getName(), unit.getId()));
        }

        return unitSelectList;
    }

    @InitBinder("categoryProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "item.name",
                "item.theme",
                "item.active",
                "category.unitId");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/structure/category/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("categoryProperties") CategoryProperties categoryProperties,
            BindingResult result, ModelMap model) {

        validator.validate(categoryProperties, result);
        if (result.hasErrors()) {
            return "secured/manager/catalog/structure/category/edit";
        }

        catalogService.updateCategoryInfo(categoryProperties.getItem(), categoryProperties.getCategory());

        model.clear();
        return "redirect:/secured/manager/catalog/structure";
    }
}
