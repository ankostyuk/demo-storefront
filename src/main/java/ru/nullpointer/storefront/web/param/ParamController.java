package ru.nullpointer.storefront.web.param;

import ru.nullpointer.storefront.web.ui.ParamInput;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ParamInputHelper;

/**
 * @author ankostyuk
 */
@Controller
public class ParamController {

    private Logger logger = LoggerFactory.getLogger(ParamController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private ModelService modelService;

    @RequestMapping(value = "/param/model/{id}", method = RequestMethod.GET)
    public String handleGetParamModel(
            @PathVariable("id") Integer modelId,
            ModelMap modelMap) {

        Model model = modelService.getModelById(modelId);

        if (model == null) {
            throw new NotFoundException();
        }

        Category category = catalogService.getCategoryById(model.getCategoryId());

        modelMap.addAttribute("_catalogItem", catalogService.getCategoryItemById(category.getId()));

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);

        ParamInput paramInput = getParamInput(category, model);
        ParamInputHelper.initBinder(paramInput, modelMap, paramModel);
        
        return "secured-common-param-input";
    }

    private ParamInput getParamInput(Category category, Model model) {
        Map<Integer, Object> paramSet = paramService.getParamSet(category.getParameterSetDescriptorId(), model.getParamSetId());
        return new ParamInput(paramSet);
    }
}
