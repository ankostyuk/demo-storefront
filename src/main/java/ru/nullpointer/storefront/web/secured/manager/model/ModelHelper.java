package ru.nullpointer.storefront.web.secured.manager.model;

import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
class ModelHelper {

    @Resource
    private CatalogService catalogService;
    @Resource
    private ModelService modelService;

    Category getCategory(Integer categoryId) {
        Category category = catalogService.getCategoryById(categoryId);
        if (category == null || !category.isParametrized()) {
            throw new NotFoundException();
        }
        return category;
    }

    Model getModel(Integer modelId) {
        Model model = modelService.getModelById(modelId);
        if (model == null) {
            throw new NotFoundException();
        }
        return model;
    }
}
