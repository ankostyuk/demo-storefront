package ru.nullpointer.storefront.web.secured.manager.catalog.category.param.group;

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
@RequestMapping("/secured/manager/catalog/category/param/group/edit/order/{id}")
public class EditOrderCategoryParamGroupController {

    private Logger logger = LoggerFactory.getLogger(EditOrderCategoryParamGroupController.class);

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

    @ModelAttribute("paramGroupProperties")
    public ParamGroupProperties getParamGroupPropertiesModelAttribute(@PathVariable("id") Integer paramGroupId) {
        ParamGroup paramGroup = getParamGroup(paramGroupId);
        ParamGroupProperties paramGroupProperties = categoryParamHelper.buildParamGroupProperties(paramGroup, false);
        return paramGroupProperties;
    }

    private CatalogItem getCategoryItem(Integer paramGroupId) {
        CatalogItem categoryItem = paramService.getCategoryItemByParamGroupId(paramGroupId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с группой параметров с id={} не найдена", paramGroupId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    private CategoryProperties getCategoryProperties(Integer paramGroupId) {
        CatalogItem categoryItem = getCategoryItem(paramGroupId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, false);
        return categoryProperties;
    }

    @ModelAttribute("orderList")
    public List<ParamGroupProperties> getOrderList(@PathVariable("id") Integer paramGroupId) {
        List<ParamGroupProperties> orderList = new ArrayList<ParamGroupProperties>();

        ParamGroup paramGroup = getParamGroup(paramGroupId);
        List<ParamGroup> paramGroupList = paramService.getParamGroupList(paramGroup.getParameterSetDescriptorId());

        for (ParamGroup pg : paramGroupList) {
            ParamGroupProperties paramGroupProperties = categoryParamHelper.buildParamGroupProperties(pg, false);
            if (paramGroup.getId().equals(pg.getId())) {
                paramGroupProperties.setCanChoose(false);
            } else {
                paramGroupProperties.setCanChoose(true);
            }
            orderList.add(paramGroupProperties);
        }

        return orderList;
    }

    @InitBinder("paramGroupProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{"paramGroup.ordinal"});
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer paramGroupId, ModelMap model) {
        model.addAttribute("categoryProperties", getCategoryProperties(paramGroupId));
        return "secured/manager/catalog/category/param/group/edit/order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer paramGroupId, @ModelAttribute("paramGroupProperties") ParamGroupProperties paramGroupProperties, BindingResult result, ModelMap model) {
        CategoryProperties categoryProperties = getCategoryProperties(paramGroupId);
        model.addAttribute("categoryProperties", categoryProperties);

        paramService.updateParamGroupOrder(paramGroupProperties.getParamGroup());

        // TODO якорь на группу папаметров?
        StringBuilder sb = new StringBuilder("redirect:/secured/manager/catalog/category/param/")//
                .append(categoryProperties.getItem().getId());

        return sb.toString();
    }
}
