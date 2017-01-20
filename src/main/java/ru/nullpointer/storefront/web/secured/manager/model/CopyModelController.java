package ru.nullpointer.storefront.web.secured.manager.model;

import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.ParamService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/manager/model/copy/{id}")
public class CopyModelController {

    @Resource
    private ModelService modelService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private ImageService imageService;
    @Resource
    private ModelHelper modelHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer modelId) {
        Model model = modelHelper.getModel(modelId);

        Category category = catalogService.getCategoryById(model.getCategoryId());
        Map<Integer, Object> paramValueMap = paramService.getParamSet(category.getParameterSetDescriptorId(), model.getParamSetId());

        imageService.copyModelImage(model);
        modelService.storeModel(model, paramValueMap);

        Assert.isTrue(!modelId.equals(model.getId()));
        return "redirect:/secured/manager/model/edit/" + model.getId();
    }
}
