package ru.nullpointer.storefront.web.offer;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.CountryService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.RegionService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.ParamInputHelper;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.ui.ParamInput;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author ankostyuk
 */
@Controller
public class OfferController {

    private Logger logger = LoggerFactory.getLogger(OfferController.class);
    //
    @Resource
    private OfferService offerService;
    @Resource
    private ModelService modelService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;
    @Resource
    private CompanyService companyService;
    @Resource
    private RegionService regionService;
    @Resource
    private ParamService paramService;
    @Resource
    private BrandService brandService;
    @Resource
    private CountryService countryService;
    @Resource
    private MatchService matchService;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private UIHelper uiHelper;
    @Resource
    private CurrencyConfig currencyConfig;

    @RequestMapping(value = "/offer")
    public String handleGet() {
        return "redirect:/";
    }

    @RequestMapping(value = "/offer/{id}", method = RequestMethod.GET)
    public String handleOfferGet(
            @PathVariable("id") Integer offerId,
            ModelMap model, HttpServletRequest request) {

        Offer offer = getOffer(offerId);

        model.addAttribute("offer", offer);
        model.addAttribute("offerAccessible", OfferService.isOfferAccessible(offer));

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Category category = catalogService.getCategoryById(offer.getCategoryId());
        List<CatalogItem> categoryPath = catalogService.getPath(category.getId());

        model.addAttribute("categoryPath", categoryPath);
        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));

        CatalogItem catalogItem = catalogService.getCategoryItemById(category.getId());
        model.addAttribute("catalogItem", catalogItem);

        Company company = companyService.getCompanyById(offer.getCompanyId());
        model.addAttribute("company", company);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        Region companyRegion = regionService.getRegionById(company.getRegionId());
        model.addAttribute("companyRegion", companyRegion);

        if (offer.getDelivery()) {
            List<Region> deliveryRegionList = regionService.getCompanyDeliveryRegionList(company.getId());
            model.addAttribute("deliveryRegionList", deliveryRegionList);
            model.addAttribute("userRegionDelivery", region != null && regionService.hasUserRegionDelivery(region, deliveryRegionList, companyRegion));
        }

        model.addAttribute("defaultCurrency", currencyConfig.getDefaultCurrency());

        if (offer.isParametrized()) {
            initParam(offer.getParamSetId(), category, model);
        } else if (offer.isModelLinked()) {
            Model offerModel = modelService.getModelById(offer.getModelId());
            initParam(offerModel.getParamSetId(), category, model);
            model.addAttribute("offerModel", offerModel);

            ModelInfo offerModelInfo = matchService.getModelInfo(offerModel.getId(), region);
            model.addAttribute("offerModelInfo", offerModelInfo);
        }

        if (offer.getBrandId() != null) {
            model.addAttribute("brand", brandService.getBrandById(offer.getBrandId()));
        }

        if (offer.getOriginCountry() != null) {
            model.addAttribute("originCountry", countryService.getCountryByAlpha2(offer.getOriginCountry()));
        }

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .append(categoryPath.get(categoryPath.size() - 1).getName())//
                .append(offer.getName())//
                .build(metadata);
        model.addAttribute("metadata", metadata);

        uiHelper.initSearchSettings(request, category.getId(), model);
        uiHelper.setCatalogTheme(categoryPath, model);

        return "offer";
    }

    private void initParam(Integer paramSetId, Category category, ModelMap model) {
        ParamModel paramModel = paramService.getParamModel(category.getParameterSetDescriptorId());
        model.addAttribute("paramModel", paramModel);

        ParamInput paramInput = new ParamInput(paramService.getParamSet(category.getParameterSetDescriptorId(), paramSetId));
        model.addAttribute("paramInput", paramInput);

        model.addAttribute("paramCountGroupMap", ParamInputHelper.buildParamCountGroupMap(paramModel, paramInput));
    }

    private Offer getOffer(Integer offerId) {
        Offer offer = offerService.getOfferById(offerId);
        if (offer == null) {
            // TODO страница с информацией о не доступности
            throw new NotFoundException("Товарное предложение с id  «" + offerId + "» не найдено");
        }
        return offer;
    }
}
