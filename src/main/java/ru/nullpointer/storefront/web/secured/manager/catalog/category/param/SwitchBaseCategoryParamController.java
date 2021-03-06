package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

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
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/category/param/switch/base/{id}")
public class SwitchBaseCategoryParamController {

    private Logger logger = LoggerFactory.getLogger(SwitchBaseCategoryParamController.class);
    //
    @Resource
    private ParamService paramService;

    private Param getParam(Integer paramId) {
        Param param = paramService.getParamById(paramId);
        if (param == null) {
            throw new NotFoundException("Параметр категории с id " + paramId + " не найден");
        }
        return param;
    }

    private void initModel(Integer paramId, ModelMap model) {
        Param param = getParam(paramId);
        ParamGroup paramGroup = paramService.getParamGroupById(param.getParamGroupId());
        CatalogItem categoryItem = paramService.getCategoryItemByParamId(param.getId());

        model.addAttribute("p", param);
        model.addAttribute("paramGroup", paramGroup);
        model.addAttribute("catalogItem", categoryItem);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer paramId, ModelMap model) {
        initModel(paramId, model);
        return "secured/manager/catalog/category/param/switch/base";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer paramId, ModelMap model) {
        Param param = getParam(paramId);
        CatalogItem categoryItem = paramService.getCategoryItemByParamId(param.getId());

        paramService.switchParamBase(param);

        // TODO якорь на группу папаметров?
        return new StringBuilder("redirect:/secured/manager/catalog/category/param/")//
                .append(categoryItem.getId())//
                .toString();
    }
}
