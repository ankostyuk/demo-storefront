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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.SelectOption;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/category/add")
public class AddCategoryController {

    private Logger logger = LoggerFactory.getLogger(AddCategoryController.class);
    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("categoryProperties")
    public CategoryProperties getCategoryProperties() {
        CategoryProperties categoryProperties = catalogStructureHelper.buildNewCategoryProperties();
        return categoryProperties;
    }

    @ModelAttribute("canAdd")
    public Boolean getCanAddModelAttribute() {
        return (!catalogService.getChildrenList(null).isEmpty());
    }

    @ModelAttribute("parentItemSelectList")
    public List<SelectOption<ItemProperties>> getParentItemSelectList() {
        List<SelectOption<ItemProperties>> parentItemSelectList = new ArrayList<SelectOption<ItemProperties>>();

        List<CatalogItem> subTree = catalogService.getSubTree(null);
        for (CatalogItem item : subTree) {
            ItemProperties parentItemProperties = catalogStructureHelper.buildItemProperties(item, false);

            if (catalogService.isSectionItem(item)) {
                parentItemProperties.setCanChoose(true);
            } else {
                parentItemProperties.setCanChoose(false);
            }
            parentItemProperties.setLevel(catalogService.getItemLevel(item));

            parentItemSelectList.add(new SelectOption<ItemProperties>(parentItemProperties.getItem().getName(), parentItemProperties));
        }

        return parentItemSelectList;
    }

    @ModelAttribute("unitSelectList")
    public List<SelectOption<Integer>> getUnitSelectList() {
        List<SelectOption<Integer>> unitSelectList = new ArrayList<SelectOption<Integer>>();

        List<Unit> allUnits = unitService.getAllUnits();
        for (Unit unit : allUnits) {
            unitSelectList.add(new SelectOption<Integer>(unit.getName(), unit.getId()));
        }

        // for null
        unitSelectList.add(new SelectOption<Integer>("", null));

        return unitSelectList;
    }

    @InitBinder("categoryProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "item.name",
                "item.theme",
                //"item.active",
                "parentItemId",
                "category.unitId");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@RequestParam(value = "parentId", required = false) Integer parentId, @ModelAttribute("categoryProperties") CategoryProperties categoryProperties) {
        if (parentId != null) {
            CatalogItem parentSection = catalogService.getSectionItemById(parentId);
            if (parentSection == null) {
                return "redirect:/secured/manager/catalog/structure";
            }
        }
        categoryProperties.setParentItemId(parentId);
        return "secured/manager/catalog/structure/category/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("categoryProperties") CategoryProperties categoryProperties,
            BindingResult result, ModelMap model) throws CatalogItemException {
        validator.validate(categoryProperties, result);
        if (result.hasErrors()) {
            return "secured/manager/catalog/structure/category/add";
        }

        // TODO try catch ?
        catalogService.addCategory(categoryProperties.getItem(), categoryProperties.getCategory(), categoryProperties.getParentItemId());

        model.clear();
        return "redirect:/secured/manager/catalog/structure";
    }
}
