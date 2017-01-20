package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.CategoryProperties;
import ru.nullpointer.storefront.web.ui.catalog.ParamGroupProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/category/param/{id}")
public class CategoryParamController {

    private Logger logger = LoggerFactory.getLogger(CategoryParamController.class);
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;
    @Autowired
    private CategoryParamHelper categoryParamHelper;

    private CatalogItem getCategoryItem(Integer categoryId) {
        CatalogItem categoryItem = catalogService.getCategoryItemById(categoryId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с id={} не найдена", categoryId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    private CategoryProperties getCategoryProperties(Integer categoryId) {
        CatalogItem categoryItem = getCategoryItem(categoryId);
        CategoryProperties categoryProperties = catalogStructureHelper.buildCategoryProperties(categoryItem, false);
        return categoryProperties;
    }

    private List<ParamGroupProperties> getCategoryParamGroupList(Category category, Boolean base) {
        Integer psdId = category.getParameterSetDescriptorId();
        if (psdId != null) {
            // категория с параметрами
            List<ParamGroupProperties> paramGroupProps = new ArrayList<ParamGroupProperties>();
            List<ParamGroup> paramGroupList = paramService.getParamGroupList(psdId);
            for (ParamGroup paramGroup : paramGroupList) {
                paramGroupProps.add(categoryParamHelper.buildParamGroupProperties(paramGroup, base));
            }
            return paramGroupProps;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer categoryId,
            @RequestParam(value = "base", required = false, defaultValue = "false") Boolean base,
            ModelMap model) {
        CategoryProperties categoryProperties = getCategoryProperties(categoryId);
        model.addAttribute("categoryProperties", categoryProperties);
        model.addAttribute("paramGroupList", getCategoryParamGroupList(categoryProperties.getCategory(), base));
        model.addAttribute("baseParams", base);

        return "secured/manager/catalog/category/param";
    }
}
