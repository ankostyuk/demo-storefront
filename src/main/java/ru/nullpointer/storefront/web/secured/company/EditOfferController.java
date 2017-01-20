package ru.nullpointer.storefront.web.secured.company;

import ru.nullpointer.storefront.web.ui.ParamInput;
import java.util.Map;
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
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.support.ParamInputHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/offer/edit/{id}")
public class EditOfferController {

    private Logger logger = LoggerFactory.getLogger(EditOfferController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private OfferService offerService;
    @Resource
    private ImageService imageService;
    @Resource
    private UnitService unitService;
    @Resource
    private ParamService paramService;
    @Resource
    private ModelService modelService;
    @Resource
    private OfferHelper offerHelper;
    //
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("offer")
    public Offer getOffer(@PathVariable("id") Integer offerId) {
        return offerHelper.getOffer(offerId);
    }

    @InitBinder("offer")
    public void initBinder(WebDataBinder binder) {
        offerHelper.initBinder(binder);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @ModelAttribute("offer") Offer offer,
            ModelMap model) {

        Category category = catalogService.getCategoryById(offer.getCategoryId());
        initModel(category, model);

        if (category.isParametrized()) {
            ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
            model.addAttribute("paramModel", paramModel);

            ParamInput paramInput = getParamInput(category, offer);
            ParamInputHelper.initBinder(paramInput, model, paramModel);
        }
        // необходимо использовать явное имя view, для того чтобы обрабатывался параметр {id}
        return "secured/company/offer/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("offer") Offer offer, BindingResult result,
            @RequestParam(value = "redirect", required = false) String redirect,
            @RequestParam(value = "deleteOfferImage", required = false) Boolean deleteOfferImage,
            @RequestParam(value = "offerImage", required = false) MultipartFile imageFile,
            ModelMap model, WebRequest request) {

        Category category = catalogService.getCategoryById(offer.getCategoryId());
        initModel(category, model);

        ParamInput paramInput = null;
        if (category.isParametrized()) {
            if (!offer.isModelLinked()) {
                ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
                model.addAttribute("paramModel", paramModel);

                paramInput = getParamInput(category, offer);
                ParamInputHelper.initBinder(paramInput, model, paramModel)//
                        .bindAndValidate(request);
            }
        }

        logger.debug("offer: {}", offer);
        logger.debug("deleteOfferImage: {}", deleteOfferImage);
        OfferHelper.logImageFile(imageFile, logger);

        validator.validate(offer, result);
        offerHelper.validateActualDate(offer, result);
        if (result.hasErrors() || (paramInput != null && paramInput.hasErrors())) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(imageFile)) {
                model.addAttribute("reselectOfferImage", true);
            }
            return "secured/company/offer/edit";
        }

        if (deleteOfferImage != null && deleteOfferImage) {
            // удалить изображение
            imageService.setOfferImage(offer, null);
        } else {
            if (imageService.isValidImage(imageFile)) {
                imageService.setOfferImage(offer, imageFile);
            }
        }

        Map<Integer, Object> paramValueMap = paramInput != null ? paramInput.getParamValueMap() : null;

        offerService.updateOfferInfo(offer, paramValueMap);

        if (redirect != null) {
            logger.debug("форсируем редирект на: {}", redirect);
            return "redirect:" + redirect;
        }
        return OfferHelper.buildRedirectUrl(offer.getCategoryId(), CompanyOfferSorting.DATE_EDITED_DESCENDING);
    }

    private void initModel(Category category, ModelMap model) {
        model.addAttribute("catalogItem", catalogService.getCategoryItemById(category.getId()));
        model.addAttribute("category", category);
        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));
        model.addAttribute("path", catalogService.getPath(category.getId()));
        model.addAttribute("countryList", offerHelper.getCountryList());
        model.addAttribute("currencyList", offerHelper.getCurrencyList());
    }

    private ParamInput getParamInput(Category category, Offer offer) {
        Map<Integer, Object> paramSet = null;
        if (offer.isModelLinked()) {
            Model model = modelService.getModelById(offer.getModelId());
            paramSet = paramService.getParamSet(category.getParameterSetDescriptorId(), model.getParamSetId());
        } else if (offer.isParametrized()) {
            paramSet = paramService.getParamSet(category.getParameterSetDescriptorId(), offer.getParamSetId());
        }
        return (paramSet != null ? new ParamInput(paramSet) : new ParamInput());
    }
}
