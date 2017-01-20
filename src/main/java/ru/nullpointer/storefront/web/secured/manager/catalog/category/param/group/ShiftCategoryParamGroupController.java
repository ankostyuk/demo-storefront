package ru.nullpointer.storefront.web.secured.manager.catalog.category.param.group;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.service.ParamService;
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
public class ShiftCategoryParamGroupController {

    private Logger logger = LoggerFactory.getLogger(ShiftCategoryParamGroupController.class);

    @Resource
    private ParamService paramService;

    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    @Autowired
    private CategoryParamHelper categoryParamHelper;

    private ParamGroup getParamGroup(Integer paramGroupId) {
        ParamGroup paramGroup = paramService.getParamGroupById(paramGroupId);
        if (paramGroup == null) {
            logger.debug("### Группа параметров категории с id={} не найдена", paramGroupId);
            throw new NotFoundException();
        }
        return paramGroup;
    }

    private CatalogItem getCategoryItem(Integer paramGroupId) {
        CatalogItem categoryItem = paramService.getCategoryItemByParamGroupId(paramGroupId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с группой параметров с id={} не найдена", paramGroupId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    public void buildModel(Integer paramGroupId, boolean shiftUp, ModelMap model) {
        ParamGroup paramGroup = getParamGroup(paramGroupId);
        ParamGroupProperties paramGroupProperties = categoryParamHelper.buildParamGroupProperties(paramGroup, false);
        CatalogItem categoryItem = getCategoryItem(paramGroupId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, false);
        model.addAttribute("paramGroupProperties", paramGroupProperties);
        model.addAttribute("categoryProperties", categoryProperties);
        model.addAttribute("shiftUp", shiftUp);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/group/shift/up/{id}", method = RequestMethod.GET)
    public String handleShiftUpGet(@PathVariable("id") Integer paramGroupId, ModelMap model) {
        buildModel(paramGroupId, true, model);
        return "secured/manager/catalog/category/param/group/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/group/shift/down/{id}", method = RequestMethod.GET)
    public String handleShiftDownGet(@PathVariable("id") Integer paramGroupId, ModelMap model) {
        buildModel(paramGroupId, false, model);
        return "secured/manager/catalog/category/param/group/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/group/shift/up/{id}", method = RequestMethod.POST)
    public String handleShiftUpPost(@PathVariable("id") Integer paramGroupId) {
        return shiftParamGroup(paramGroupId, true);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/group/shift/down/{id}", method = RequestMethod.POST)
    public String handleShiftDownPost(@PathVariable("id") Integer paramGroupId) {
        return shiftParamGroup(paramGroupId, false);
    }

    private String shiftParamGroup(Integer paramGroupId, boolean shiftUp) {
        ParamGroup paramGroup = getParamGroup(paramGroupId);
        CatalogItem categoryItem = getCategoryItem(paramGroupId);

        paramGroup.setOrdinal(shiftUp ? paramGroup.getOrdinal() - 1 : paramGroup.getOrdinal() + 1);
        paramService.updateParamGroupOrder(paramGroup);

        // TODO якорь на группу папаметров?
        StringBuilder sb = new StringBuilder("redirect:/secured/manager/catalog/category/param/")//
                .append(categoryItem.getId());

        return sb.toString();
    }
}
