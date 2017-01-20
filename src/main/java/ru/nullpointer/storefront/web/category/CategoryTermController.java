package ru.nullpointer.storefront.web.category;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.UIHelper;

/**
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Controller
public class CategoryTermController {

    private Logger logger = LoggerFactory.getLogger(CategoryTermController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/category/{id}/term")
    public String handleCategoryTermGet(
            @PathVariable("id") Integer categoryItemId,
            ModelMap model, HttpServletRequest request) {

        CatalogItem catalogItem = catalogService.getCategoryItemById(categoryItemId);
        if (catalogItem == null || !catalogItem.getActive()) {
            throw new NotFoundException("Категория с id '" + catalogItem + "' не найдена");
        }

        Category category = catalogService.getCategoryById(categoryItemId);
        if (!category.isParametrized()) {
            throw new NotFoundException("Категория с id '" + catalogItem + "' не имеет параметров");
        }

        initModel(catalogItem, model);

        model.addAttribute("paramList", paramService.getParamWithDescriptionList(category.getParameterSetDescriptorId()));

        uiHelper.initSearchSettings(request, catalogItem.getId(), model);

        return "category/term";
    }

    @RequestMapping(value = "/category/{id}/term/{paramId}")
    public String handleCategoryTermByIdGet(
            @PathVariable("id") Integer categoryItemId,
            @PathVariable("paramId") Integer paramId,
            ModelMap model) {

        Param param = paramService.getParamById(paramId);
        if (param == null) {
            throw new NotFoundException("Параметр с id '" + paramId + "' не найден");
        }

        model.addAttribute("_param", param);

        return "category-term";
    }

    private void initModel(CatalogItem catalogItem, ModelMap model) {
        List<CatalogItem> path = catalogService.getPath(catalogItem.getId());
        model.addAttribute("path", path);

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .append(catalogItem.getName())//
                .appendMessage("ui.category.term.title")//
                .build(metadata);
        model.addAttribute("metadata", metadata);

        uiHelper.setCatalogTheme(path, model);
    }
}
