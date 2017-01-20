package ru.nullpointer.storefront.web.secured.manager.model;

import javax.annotation.Resource;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ParamInputHelper;
import ru.nullpointer.storefront.web.ui.ParamInput;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/manager/model/edit/{id}")
public class EditModelController {

    @Resource
    private ModelService modelService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private BrandService brandService;
    @Resource
    private ImageService imageService;
    //
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("model")
    public Model getModel(@PathVariable("id") Integer modelId) {
        Model model = modelService.getModelById(modelId);
        if (model == null) {
            throw new NotFoundException();
        }
        return model;
    }

    @InitBinder("model")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "brandId",
                "name",
                "description",
                "vendorCode",
                "keywords",
                "site");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @ModelAttribute("model") Model model,
            ModelMap modelMap) {

        Category category = catalogService.getCategoryById(model.getCategoryId());
        initModelMap(category, model, modelMap);

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);

        ParamInput paramInput = getParamInput(category, model);
        ParamInputHelper.initBinder(paramInput, modelMap, paramModel, false);

        return "secured/manager/model/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("model") Model model, BindingResult result,
            @RequestParam(value = "deleteImage", required = false) Boolean deleteImage,
            @RequestParam(value = "image", required = false) MultipartFile image,
            ModelMap modelMap, WebRequest request) {

        Category category = catalogService.getCategoryById(model.getCategoryId());
        initModelMap(category, model, modelMap);

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);

        ParamInput paramInput = getParamInput(category, model);
        ParamInputHelper.initBinder(paramInput, modelMap, paramModel, false)//
                .bindAndValidate(request);

        validator.validate(model, result);
        if (result.hasErrors() || paramInput.hasErrors()) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(image)) {
                modelMap.addAttribute("reselectImage", true);
            }
            return "secured/manager/model/edit";
        }

        if (deleteImage != null && deleteImage) {
            // удалить изображение
            imageService.setModelImage(model, null);
        } else {
            if (imageService.isValidImage(image)) {
                imageService.setModelImage(model, image);
            }
        }

        modelService.updateModelInfo(model, paramInput.getParamValueMap());

        return "redirect:/secured/manager/model/" + model.getCategoryId();
    }

    private void initModelMap(Category category, Model model, ModelMap modelMap) {
        modelMap.addAttribute("catalogItem", catalogService.getCategoryItemById(category.getId()));
        modelMap.addAttribute("canDelete", modelService.canDeleteModel(model));

        Integer brandId = model.getBrandId();
        if (brandId != null) {
            Brand brand = brandService.getBrandById(model.getBrandId());
            if (brand == null) {
                model.setBrandId(null);
            } else {
                modelMap.addAttribute("brand", brand);
            }
        }
    }

    private ParamInput getParamInput(Category category, Model model) {
        return new ParamInput(paramService.getParamSet(category.getParameterSetDescriptorId(), model.getParamSetId()));
    }
}
