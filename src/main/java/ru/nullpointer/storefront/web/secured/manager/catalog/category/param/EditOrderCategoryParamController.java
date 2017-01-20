package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

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
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;
import ru.nullpointer.storefront.web.ui.catalog.ParamGroupProperties;
import ru.nullpointer.storefront.web.ui.catalog.ParamProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/category/param/edit/order/{id}")
public class EditOrderCategoryParamController {

    private Logger logger = LoggerFactory.getLogger(EditOrderCategoryParamController.class);
    //
    @Resource
    private ParamService paramService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;
    @Autowired
    private CategoryParamHelper categoryParamHelper;

    private Param getParam(Integer paramId) {
        Param param = paramService.getParamById(paramId);
        if (param == null) {
            logger.debug("### Параметр категории с id={} не найден", paramId);
            throw new NotFoundException();
        }
        return param;
    }

    @ModelAttribute("paramProperties")
    public ParamProperties getParamPropertiesModelAttribute(@PathVariable("id") Integer paramId) {
        Param param = getParam(paramId);
        ParamProperties paramProperties = categoryParamHelper.buildParamProperties(param);
        return paramProperties;
    }

    private ParamGroup getParamGroup(Integer paramGroupId) {
        ParamGroup paramGroup = paramService.getParamGroupById(paramGroupId);
        if (paramGroup == null) {
            logger.debug("### Группа параметров категории с id={} не найдена", paramGroupId);
            throw new NotFoundException();
        }
        return paramGroup;
    }

    private CatalogItem getCategoryItem(Integer paramId) {
        CatalogItem categoryItem = paramService.getCategoryItemByParamId(paramId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с параметром с id={} не найдена", paramId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    private ParamGroupProperties getParamGroupProperties(Integer paramGroupId) {
        ParamGroup paramGroup = getParamGroup(paramGroupId);
        ParamGroupProperties paramGroupProperties = categoryParamHelper.buildParamGroupProperties(paramGroup, false);
        return paramGroupProperties;
    }

    private CategoryProperties getCategoryProperties(Integer paramId) {
        CatalogItem categoryItem = getCategoryItem(paramId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, false);
        return categoryProperties;
    }

    @ModelAttribute("orderList")
    public List<ParamProperties> getOrderList(@PathVariable("id") Integer paramId) {
        List<ParamProperties> orderList = new ArrayList<ParamProperties>();

        Param param = getParam(paramId);
        List<Param> paramList = paramService.getParamList(param.getParamGroupId());

        for (Param p : paramList) {
            ParamProperties paramProperties = categoryParamHelper.buildParamProperties(p);
            if (param.getId().equals(p.getId())) {
                paramProperties.setCanChoose(false);
            } else {
                paramProperties.setCanChoose(true);
            }
            orderList.add(paramProperties);
        }

        return orderList;
    }

    @InitBinder("paramProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
                    "param.ordinal"
                });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer paramId,
            @ModelAttribute("paramProperties") ParamProperties paramProperties,
            ModelMap model) {
        model.addAttribute("paramGroupProperties", getParamGroupProperties(paramProperties.getParam().getParamGroupId()));
        model.addAttribute("categoryProperties", getCategoryProperties(paramId));
        return "secured/manager/catalog/category/param/edit/order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer paramId,
            @ModelAttribute("paramProperties") ParamProperties paramProperties,
            BindingResult result,
            ModelMap model) {
        ParamGroupProperties paramGroupProperties = getParamGroupProperties(paramProperties.getParam().getParamGroupId());
        model.addAttribute("paramGroupProperties", paramGroupProperties);

        CategoryProperties categoryProperties = getCategoryProperties(paramId);
        model.addAttribute("categoryProperties", categoryProperties);

        paramService.updateParamOrder(paramProperties.getParam());

        // TODO якорь на группу папаметров?
        StringBuilder sb = new StringBuilder("redirect:/secured/manager/catalog/category/param/")//
                .append(categoryProperties.getItem().getId());

        return sb.toString();
    }
}
