package ru.nullpointer.storefront.web.secured.manager.catalog.structure.category;

import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/category/copy/{id}")
public class CopyCategoryController {

    private Logger logger = LoggerFactory.getLogger(CopyCategoryController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;

    private CatalogItem getCategoryItem(Integer categoryId) {
        CatalogItem categoryItem = catalogService.getCategoryItemById(categoryId);
        if (categoryItem == null) {
            logger.debug("### Категория каталога с id={} не найдена", categoryId);
            throw new NotFoundException();
        }
        return categoryItem;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer categoryId) throws CatalogItemException {
        CatalogItem item = getCategoryItem(categoryId);

        CatalogItem parentItem = catalogService.getParentItem(item.getId());
        Assert.notNull(parentItem);

        Category category = catalogService.getCategoryById(item.getId());
        Assert.notNull(category);
        Integer psd = category.getParameterSetDescriptorId();

        item.setId(null);
        item.setPath(null);
        item.setActive(Boolean.FALSE);

        category.setId(null);
        category.setParameterSetDescriptorId(null);

        catalogService.addCategory(item, category, parentItem.getId());

        if (psd != null) {
            for (ParamGroup paramGroup : paramService.getParamGroupList(psd)) {
                Integer paramGroupId = paramGroup.getId();
                paramService.addParamGroup(category, paramGroup);
                for (Param param : paramService.getParamList(paramGroupId)) {
                    switch (param.getType()) {
                        case BOOLEAN:
                            paramService.addParam(paramGroup, param);
                            break;
                        case NUMBER:
                            paramService.addParam(paramGroup, param);
                            break;
                        case SELECT:
                            List<ParamSelectOption> options = paramService.getParamSelectOptions(param);
                            paramService.addParam(paramGroup, param);
                            paramService.addParamSelectOptions(param.getId(), options);
                            break;
                        default:
                            throw new RuntimeException("Неподдерживаемый тип параметра: " + param.getType());
                    }
                }
            }
        }

        return "redirect:/secured/manager/catalog/structure/category/edit/" + category.getId();
    }
}
