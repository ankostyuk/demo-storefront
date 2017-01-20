package ru.nullpointer.storefront.web.secured.company.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.SecurityService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.company.CompanySearchBuilder;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.ui.Target;

/**
 * @author ankostyuk
 */
@Controller
public class CompanySearchController {

    private Logger logger = LoggerFactory.getLogger(CompanySearchController.class);
    //
    private static final int SUGGEST_COUNT = 10;
    private static final int FORCE_OFFER_RESULT_PAGE_COUNT = 6;
    private static final int DEFAULT_PAGE_SIZE = 10;
    //
    @Resource
    private CompanySearchBuilder companySearch;
    @Resource
    private CompanySearchHelper companySearchHelper;
    @Resource
    private SecurityService securityService;
    @Resource
    private SearchUtils searchUtils;
    @Resource
    private OfferService offerService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;
    @Resource
    private CurrencyConfig currencyConfig;

    @RequestMapping(value = "/secured/company/search/suggest", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Target> handleSuggestGet(
            @RequestParam(value = "text", required = false) String text) {
        List<Document> docs = companySearch.suggestSearchByText(
                text, SUGGEST_COUNT,
                SearchUtils.SORT_NAME, // TODO other?
                buildCompanyFilter());

        List<Target> resultList = new ArrayList<Target>(docs.size());

        for (Document doc : docs) {
            resultList.add(companySearchHelper.buildTarget(doc));
        }
        return resultList;
    }

    @RequestMapping(value = "/secured/company/search", method = RequestMethod.GET)
    public String handleSearchGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            ModelMap model, HttpServletRequest request) {

        if (text == null) {
            return "redirect:/secured/company"; // TODO
        }

        text = UIHelper.normalizeSearchText(text);
        model.addAttribute("text", text);

        if (text.isEmpty()) {
            model.addAttribute("emptyText", true);
            return "secured/company/search/empty";
        }

        String searchText = text;

        int resultSize = 0;

        boolean repeat = false;

        do {
            // offers
            PaginatedQueryResult<Document> offerResult =
                    getOfferSearchResult(searchText, page, model);

            List<Document> offerResultList = offerResult.getList();

            if (!offerResultList.isEmpty()) {
                resultSize += offerResultList.size();
                buildOfferResult(offerResult, model);
            }

            if (repeat) {
                break;
            }

            if (resultSize == 0) {
                if (page == 1) {
                    String correctedText = companySearch.suggestCorrectSearchText(
                            searchText, null);

                    if (correctedText == null || correctedText.length() < 1) {
                        break;
                    }

                    searchText = correctedText;
                    model.addAttribute("correctedText", correctedText);

                    repeat = true;
                } else {
                    page = 1;
                    repeat = true;
                }
            }
        } while (repeat);

        if (resultSize == 0) {
            model.addAttribute("emptyResult", true);
            return "secured/company/search/empty";
        }

        return "secured/company/search/result";
    }

    private PaginatedQueryResult<Document> getOfferSearchResult(
            String text, Integer page, ModelMap model) {

        PageConfig pageConfig = new PageConfig(page, DEFAULT_PAGE_SIZE);

        int resultCount = pageConfig.getPageSize() * (pageConfig.getPageNumber() + FORCE_OFFER_RESULT_PAGE_COUNT - 1) + 1;

        Filter filter = buildCompanyFilter(); // TODO + offer filter если будут иные результаты поиска

        PaginatedQueryResult<Document> searchResult = // TODO other sort?
                companySearch.searchPaginatedByText(text, pageConfig, resultCount, SearchUtils.SORT_NAME, filter);

        model.addAttribute("offerDisplayedPageSideCount", FORCE_OFFER_RESULT_PAGE_COUNT / 2);
        model.addAttribute("offerTotalMore", resultCount <= searchResult.getTotal());

        return searchResult;
    }

    private void buildOfferResult(PaginatedQueryResult<Document> offerResult, ModelMap model) {

        Set<Integer> offerIdSet = new LinkedHashSet<Integer>(offerResult.getList().size());
        for (Document doc : offerResult) {
            offerIdSet.add(companySearchHelper.getId(doc));
        }

        Map<Integer, Offer> offerMap = offerService.getOfferMap(offerIdSet);

        List<Offer> offerList = new ArrayList<Offer>(offerIdSet.size());
        for (Integer id : offerIdSet) {
            offerList.add(offerMap.get(id));
        }

        model.addAttribute("offerResult", new PaginatedQueryResult<Offer>(
                new PageConfig(offerResult.getPageNumber(), offerResult.getPageSize()), offerList, offerResult.getTotal()));

        Set<Integer> resultCategoryIdSet = new HashSet<Integer>();
        for (Offer offer : offerList) {
            resultCategoryIdSet.add(offer.getCategoryId());
        }

        Map<Integer, List<CatalogItem>> pathMap = null;
        pathMap = catalogService.getCategoryPathMap(resultCategoryIdSet);
        model.addAttribute("pathMap", pathMap);

        model.addAttribute("unitMap", unitService.getCategoryUnitMap(resultCategoryIdSet));

        model.addAttribute("defaultCurrency", currencyConfig.getDefaultCurrency());
    }

    private Filter buildCompanyFilter() {
        return searchUtils.buildCompanyIdFilter(securityService.getAuthenticatedCompanyId());
    }
}
