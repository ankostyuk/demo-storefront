package ru.nullpointer.storefront.web.secured.company;

import ru.nullpointer.storefront.web.ui.ParamInput;
import java.math.BigDecimal;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.service.ImageService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ParamInputHelper;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/company/offer/add")
public class AddOfferController {

    private Logger logger = LoggerFactory.getLogger(AddOfferController.class);
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private ImageService imageService;
    @Resource
    private OfferService offerService;
    @Resource
    private SecurityService securityService;
    @Resource
    private ParamService paramService;
    @Resource
    private UnitService unitService;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private OfferHelper offerHelper;
    //
    @Autowired
    private BeanValidator validator;

    public Category getCategory(Integer id) {
        Category category = catalogService.getCategoryById(id);
        if (category == null) {
            throw new NotFoundException("Категория с номером «" + id + "» не найдена");
        }
        return category;
    }

    @ModelAttribute("offer")
    public Offer getOffer() {
        Company company = securityService.getAuthenticatedCompany();

        Offer offer = new Offer();

        offer.setRatio(BigDecimal.valueOf(1));
        offer.setCurrency(currencyConfig.getDefaultCurrency());
        offer.setActive(true);
        offer.setAvailable(true);
        offer.setDelivery(StringUtils.isNotBlank(company.getDeliveryConditions()));
        offer.setActualDate(offerHelper.getDefaultActualDate());

        return offer;
    }

    @InitBinder("offer")
    public void initBinder(WebDataBinder binder) {
        offerHelper.initBinder(binder);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@RequestParam(value = "categoryId") Integer categoryId, ModelMap model) {

        Category category = getCategory(categoryId);
        initModel(category, model);

        if (category.isParametrized()) {
            ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
            model.addAttribute("paramModel", paramModel);
            model.addAttribute("paramInput", new ParamInput());
        }

        return "secured/company/offer/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @RequestParam(value = "categoryId") Integer categoryId,
            @ModelAttribute("offer") Offer offer, BindingResult result,
            @RequestParam(value = "offerImage", required = false) MultipartFile imageFile,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model, WebRequest request) {

        Category category = getCategory(categoryId);
        initModel(category, model);

        ParamInput paramInput = null;
        if (category.isParametrized()) {
            ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
            model.addAttribute("paramModel", paramModel);

            paramInput = new ParamInput();
            ParamInputHelper.initBinder(paramInput, model, paramModel)//
                    .bindAndValidate(request);
        }

        offer.setCategoryId(category.getId());

        logger.debug("offer: {}", offer);
        OfferHelper.logImageFile(imageFile, logger);

        validator.validate(offer, result);
        offerHelper.validateActualDate(offer, result);
        if (result.hasErrors() || (paramInput != null && paramInput.hasErrors())) {
            // подсказать что нужно выбрать изображение заново
            if (imageService.isValidImage(imageFile)) {
                model.addAttribute("reselectOfferImage", true);
            }
            return "secured/company/offer/add";
        }

        // Все ОК - сохраняем
        if (imageService.isValidImage(imageFile)) {
            imageService.setOfferImage(offer, imageFile);
        }

        Map<Integer, Object> paramValueMap = paramInput != null ? paramInput.getParamValueMap() : null;

        offerService.storeOffer(offer, paramValueMap);

        if (redirect != null) {
            logger.debug("форсируем редирект на: {}", redirect);
            return "redirect:" + redirect;
        }
        return OfferHelper.buildRedirectUrl(offer.getCategoryId(), CompanyOfferSorting.DATE_CREATED_DESCENDING);
    }

    private void initModel(Category category, ModelMap model) {
        model.addAttribute("catalogItem", catalogService.getCategoryItemById(category.getId()));
        model.addAttribute("category", category);
        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));
        model.addAttribute("path", catalogService.getPath(category.getId()));
        model.addAttribute("countryList", offerHelper.getCountryList());
        model.addAttribute("currencyList", offerHelper.getCurrencyList());
    }
}
