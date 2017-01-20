package ru.nullpointer.storefront.web.secured.manager.model;

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
import ru.nullpointer.storefront.web.support.ParamInputHelper;
import ru.nullpointer.storefront.web.ui.ParamInput;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class AddModelController {

    private Logger logger = LoggerFactory.getLogger(AddModelController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private ParamService paramService;
    @Resource
    private ModelService modelService;
    @Resource
    private BrandService brandService;
    @Resource
    private ImageService imageService;
    @Resource
    private ModelHelper modelHelper;
    //
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("model")
    public Model getModel() {
        Model model = new Model();
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

    @RequestMapping(value = "/secured/manager/model/add/{id}", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer cateoryId,
            @ModelAttribute("model") Model model,
            ModelMap modelMap) {

        Category category = modelHelper.getCategory(cateoryId);
        initModelMap(category, model, modelMap);

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);
        modelMap.addAttribute("paramInput", new ParamInput());

        return "secured/manager/model/add";
    }

    @RequestMapping(value = "/secured/manager/model/add/{id}", method = RequestMethod.POST)
    public String handlePost(
            @PathVariable(value = "id") Integer categoryId,
            @ModelAttribute("model") Model model, BindingResult result,
            @RequestParam(value = "image", required = false) MultipartFile image,
            ModelMap modelMap, WebRequest request) {

        Category category = modelHelper.getCategory(categoryId);
        initModelMap(category, model, modelMap);

        model.setCategoryId(categoryId);

        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        modelMap.addAttribute("paramModel", paramModel);

        ParamInput paramInput = new ParamInput();
        ParamInputHelper.initBinder(paramInput, modelMap, paramModel, false)//
                .bindAndValidate(request);


        validator.validate(model, result);
        if (result.hasErrors() || paramInput.hasErrors()) {
            return "secured/manager/model/add";
        }

        if (imageService.isValidImage(image)) {
            imageService.setModelImage(model, image);
        }

        modelService.storeModel(model, paramInput.getParamValueMap());

        return "redirect:/secured/manager/model/" + categoryId;
    }

    private void initModelMap(Category category, Model model, ModelMap modelMap) {
        modelMap.addAttribute("catalogItem", catalogService.getCategoryItemById(category.getId()));

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
}
