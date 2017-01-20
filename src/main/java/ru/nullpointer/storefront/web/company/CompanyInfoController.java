package ru.nullpointer.storefront.web.company;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Contact;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.ContactService;
import ru.nullpointer.storefront.service.RegionService;
import ru.nullpointer.storefront.web.Metadata;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.support.CatalogHelper;
import ru.nullpointer.storefront.web.support.CatalogHelper.RootSectionRetriever;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.web.support.UserSessionHelper;

/**
 * @author ankostyuk
 */
@Controller
public class CompanyInfoController {

    @Resource
    private CompanyService companyService;
    @Resource
    private ContactService contactService;
    @Resource
    private RegionService regionService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private CatalogHelper catalogHelper;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/company")
    public String handleGet() {
        return "redirect:/";
    }

    @RequestMapping(value = "/company/{id}", method = RequestMethod.GET)
    public String handleCompanyGet(
            @PathVariable("id") Integer companyId,
            ModelMap model) {

        Company company = getCompany(companyId);

        model.addAttribute("company", company);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Region companyRegion = regionService.getRegionById(company.getRegionId());
        model.addAttribute("companyRegion", companyRegion);
        model.addAttribute("companyRegionPath", regionService.getRegionPath(companyRegion));

        List<Contact> contactList = contactService.getAccountContactList(companyId);
        model.addAttribute("contactList", contactList);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        List<Region> deliveryRegionList = regionService.getCompanyDeliveryRegionList(companyId);
        model.addAttribute("deliveryRegionList", deliveryRegionList);
        model.addAttribute("userRegionDelivery", region != null && regionService.hasUserRegionDelivery(region, deliveryRegionList, companyRegion));

        buildCategoryList(company, model);

        Metadata metadata = new Metadata();
        uiHelper.titleBuilder()//
                .appendMessage("ui.company.info.title")//
                .append(company.getName())//
                .build(metadata);
        model.addAttribute("metadata", metadata);

        return "company";
    }

    private Company getCompany(Integer companyId) {
        Company company = companyService.getCompanyById(companyId);
        if (company == null) {
            // TODO страница с информацией о не доступности
            throw new NotFoundException("Поставщик с id  «" + companyId + "» не найден");
        }
        return company;
    }

    private void buildCategoryList(Company company, ModelMap model) {
        List<CatalogItem> brandCategoryList = catalogService.getCompanyActiveCategoryList(company.getId());
        RootSectionRetriever rootSectionRetriever = catalogHelper.buildRootSectionRetrieverByCategoryList(brandCategoryList);
        model.addAttribute("rootSectionList", rootSectionRetriever.getRootSectionList());
        model.addAttribute("rootSectionCategoryMap", rootSectionRetriever.getRootSectionCategoryMap());
    }
}
