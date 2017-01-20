package ru.nullpointer.storefront.web.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilterClause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.CatalogItemPathComparator;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.domain.Settings;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.service.BrandService;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.MatchService;
import ru.nullpointer.storefront.service.search.SearchUtils;
import ru.nullpointer.storefront.service.search.Type;
import ru.nullpointer.storefront.service.search.catalog.CatalogSearchBuilder;
import ru.nullpointer.storefront.web.support.CatalogHelper;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.support.UIHelper;
import ru.nullpointer.storefront.web.support.UserSessionHelper;
import ru.nullpointer.storefront.web.ui.Target;

/**
 * @author ankostyuk
 */
@Controller
public class CatalogSearchController {

    private Logger logger = LoggerFactory.getLogger(CatalogSearchController.class);
    //
    private static final int SUGGEST_COUNT = 10;
    private static final int FORCE_OFFER_RESULT_PAGE_COUNT = 6;
    //
    private static Filter suggestFilter = SearchUtils.buildTypeFilter(new Type[]{Type.MODEL, Type.BRAND, Type.SECTION, Type.CATEGORY});
    //
    @Resource
    private SearchUtils searchUtils;
    @Resource
    private CatalogSearchBuilder catalogSearch;
    @Resource
    private MatchService matchService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private BrandService brandService;
    @Resource
    private UserSessionHelper userSessionHelper;
    @Resource
    private CatalogSearchHelper catalogSearchHelper;
    @Resource
    private CatalogHelper catalogHelper;
    @Resource
    private UIHelper uiHelper;

    @RequestMapping(value = "/search/suggest", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Target> handleSuggestGet(
            @RequestParam(value = "text", required = false) String text) {
        // TODO фильтровать для текущей категории?
        // TODO фильтровать для региона?
        List<Document> docs = catalogSearch.suggestSearchByText(
                text, SUGGEST_COUNT,
                SearchUtils.SORT_TYPE_ORDER, // TODO ? SearchUtils.SORT_RELEVANCE
                suggestFilter);

        List<Target> resultList = new ArrayList<Target>(docs.size());

        for (Document doc : docs) {
            resultList.add(catalogSearchHelper.buildTarget(doc));
        }

        return resultList;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String handleSearchGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "item", required = false) Integer catalogItemId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            ModelMap model, HttpServletRequest request) {

        if (text == null) {
            return "redirect:/";
        }

        List<CatalogItem> catalogItemPath = null;
        CatalogItem catalogItem = null;

        if (catalogItemId != null) {
            catalogItemPath = catalogService.getPath(catalogItemId);

            if (catalogItemPath.isEmpty()) {
                catalogItemId = null;
            } else {
                catalogItem = catalogItemPath.get(catalogItemPath.size() - 1);
                model.addAttribute("catalogItemId", catalogItemId); // TODO ?
                model.addAttribute("catalogItemPath", catalogItemPath);
            }
        }

        uiHelper.initSearchSettings(request, catalogItemId, model);

        text = UIHelper.normalizeSearchText(text);
        model.addAttribute("text", text);

        if (text.isEmpty()) {
            model.addAttribute("emptyText", true);
            return "search/empty";
        }

        String searchText = new String(text);

        UserSession userSession = userSessionHelper.getUserSession();
        model.addAttribute("userSession", userSession);

        Region region = RegionHelper.getUserRegion(userSession.getSettings());
        model.addAttribute("regionAware", region != null);

        int resultSize = 0;

        boolean repeat = false;

        do {
            // offers
            PaginatedQueryResult<Document> offerResult =
                    getOfferSearchResult(searchText, catalogItem, page, 
                    userSession.getSettings(), region, model);

            List<Document> offerResultList = offerResult.getList();

            if (!offerResultList.isEmpty()) {

                resultSize += offerResultList.size();

                buildOfferResult(offerResult, catalogItem, region, model);
            }

            if (page == 1) {
                // catalog items
                // TODO min chars?
                List<Document> catalogItemResultList = getCatalogItemSearchResult(searchText, catalogItem, model);

                if (!catalogItemResultList.isEmpty()) {

                    resultSize += catalogItemResultList.size();

                    buildCatalogItemResult(catalogItemResultList, catalogItem, model);
                }

                // brands
                // TODO min chars?
                List<Document> brandResultList =
                        buildBrandResult(getBrandSearchResult(searchText, model), catalogItem, model);

                if (!brandResultList.isEmpty()) {

                    resultSize += brandResultList.size();
                }
            }

            if (repeat) {
                break;
            }

            if (resultSize == 0) {
                if (page == 1) {
                    String correctedText = catalogSearch.suggestCorrectSearchText(
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

        if (resultSize == 0) { // TODO+
            model.addAttribute("emptyResult", true);
            return "search/empty";
        }

        return "search/result";
    }

    private PaginatedQueryResult<Document> getOfferSearchResult(
            String text, CatalogItem catalogItem, Integer page, Settings settings, 
            Region region, ModelMap model) {

        PageConfig pageConfig = new PageConfig(page, settings.getPageSize());

        int resultCount = pageConfig.getPageSize() * (pageConfig.getPageNumber() + FORCE_OFFER_RESULT_PAGE_COUNT - 1) + 1;

        // TODO ONLY PRICE FILTER
        Filter filter = SearchUtils.buildTypeFilter(new Type[]{Type.MODEL, Type.OFFER});
        filter = addFilter(filter, catalogItem, region);

        PaginatedQueryResult<Document> searchResult =
                catalogSearch.searchPaginatedByText(text, pageConfig, resultCount,
                SearchUtils.SORT_TYPE_ORDER, filter);

        model.addAttribute("offerDisplayedPageSideCount", FORCE_OFFER_RESULT_PAGE_COUNT / 2);
        model.addAttribute("offerTotalMore", resultCount <= searchResult.getTotal());

        return searchResult;
    }

    private void buildOfferResult(PaginatedQueryResult<Document> offerResult, CatalogItem catalogItem, Region region, ModelMap model) {
        List<Match> resultList = new ArrayList<Match>(offerResult.getList().size());

        for (Document doc : offerResult) {
            resultList.add(catalogSearchHelper.buildMatch(doc));
        }

        List<AbstractMatch> matchList = matchService.buildMatchResultList(resultList, region);

        model.addAttribute("offerResult", new PaginatedQueryResult<AbstractMatch>(
                new PageConfig(offerResult.getPageNumber(), offerResult.getPageSize()), matchList, offerResult.getTotal()));

        Map<Integer, List<CatalogItem>> categoryPathMap = catalogHelper.buildMatchCategoryPathMap(matchList);

        if (catalogItem != null) {
            Map<Integer, List<CatalogItem>> map = new HashMap<Integer, List<CatalogItem>>(categoryPathMap.size());
            for (Integer key : categoryPathMap.keySet()) {
                List<CatalogItem> path = categoryPathMap.get(key);
                List<CatalogItem> relativePath = catalogHelper.relativePath(path, catalogItem);
                map.put(key, relativePath == null ? path : relativePath);
            }
            categoryPathMap = map;
        }

        model.addAttribute("categoryPathMap", categoryPathMap);

        model.addAttribute("categoryUnitMap", catalogHelper.buildMatchCategoryUnitMap(matchList));
    }

    private List<Document> getCatalogItemSearchResult(
            String text, CatalogItem catalogItem, ModelMap model) {

        int catalogItemCount = catalogService.getActiveCatalogItemCount();

        if (catalogItemCount == 0) {
            return emptySearchResult().getList();
        }

        // TODO ? принять решение о постраничном выводе на основе catalogItemCount
        PageConfig pageConfig = new PageConfig(1, catalogItemCount);

        int resultCount = pageConfig.getPageSize();

        Filter filter = SearchUtils.buildTypeFilter(new Type[]{Type.SECTION, Type.CATEGORY});
        filter = addFilter(filter, catalogItem, null);

        PaginatedQueryResult<Document> searchResult =
                catalogSearch.searchPaginatedByText(text, pageConfig, resultCount, SearchUtils.SORT_INDEX_ORDER, filter);

        return searchResult.getList();
    }

    private void buildCatalogItemResult(List<Document> catalogItemResultList, CatalogItem catalogItem, ModelMap model) {
        Set<Integer> itemIdSet = new HashSet<Integer>(catalogItemResultList.size());

        for (Document doc : catalogItemResultList) {
            itemIdSet.add(catalogSearchHelper.getId(doc));
        }

        List<List<CatalogItem>> pathList = catalogService.getMergedPathList(itemIdSet);

        if (catalogItem != null) {
            List<List<CatalogItem>> list = new ArrayList<List<CatalogItem>>(pathList.size());
            for (List<CatalogItem> path : pathList) {
                List<CatalogItem> relativePath = catalogHelper.relativePath(path, catalogItem);
                list.add(relativePath == null ? path : relativePath);
            }
            pathList = list;
        }

        Collections.sort(pathList, new CatalogItemPathComparator());

        model.addAttribute("catalogItemResult", pathList);
    }

    private List<Document> getBrandSearchResult(
            String text, ModelMap model) {

        int brandCount = brandService.getBrandCount();

        if (brandCount == 0) {
            return emptySearchResult().getList();
        }

        // TODO ? принять решение о постраничном выводе на основе brandCount
        PageConfig pageConfig = new PageConfig(1, brandCount);

        int resultCount = pageConfig.getPageSize();

        Filter filter = SearchUtils.buildTypeFilter(Type.BRAND);

        PaginatedQueryResult<Document> searchResult =
                catalogSearch.searchPaginatedByText(text, pageConfig, resultCount, SearchUtils.SORT_INDEX_ORDER, filter);

        return searchResult.getList();
    }

    /*
    // TODO обдумать.
    Фильтрация по категории здесь!
    "Некрасиво", но, если фильтровать в индексе - потянет сложную логику и нагрузку,
    при изменении доступности предложений и активности разделов, категорий.
    Обратить внимание при реальном постраничном выводе.
     */
    private List<Document> buildBrandResult(List<Document> brandResultList, CatalogItem catalogItem, ModelMap model) {
        List<Document> filteredDocumentList = null;

        Set<Integer> brandIdSet = new HashSet<Integer>(brandResultList.size());

        for (Document doc : brandResultList) {
            brandIdSet.add(catalogSearchHelper.getId(doc));
        }

        List<Brand> brandList = null;

        if (catalogItem == null) {
            brandList = brandService.getBrandListByIdSet(brandIdSet);
            filteredDocumentList = brandResultList;
        } else {
            Set<Integer> categoryIdSet = new HashSet<Integer>();

            if (catalogService.isCategoryItem(catalogItem)) {
                categoryIdSet.add(catalogItem.getId());
            } else {
                List<CatalogItem> categoryList = catalogService.getSubCategories(catalogItem.getId());
                for (CatalogItem categoryItem : categoryList) {
                    categoryIdSet.add(categoryItem.getId());
                }
            }

            brandList = brandService.getIntersectionList(brandIdSet, categoryIdSet);

            filteredDocumentList = new ArrayList<Document>(brandList.size());
            for (Brand brand : brandList) {
                for (Document doc : brandResultList) {
                    if (brand.getId().equals(catalogSearchHelper.getId(doc))) {
                        filteredDocumentList.add(doc);
                        break;
                    }
                }
            }
        }

        model.addAttribute("brandResult", brandList);

        return filteredDocumentList;
    }

    private Filter addFilter(Filter filter, CatalogItem catalogItem, Region region) {
        if (catalogItem == null && region == null) {
            return filter;
        }

        BooleanFilter booleanFilter = new BooleanFilter();

        booleanFilter.add(new FilterClause(filter, Occur.MUST));

        if (catalogItem != null) {
            booleanFilter.add(new FilterClause(searchUtils.buildCatalogItemFilter(catalogItem.getId()), Occur.MUST));
        }

        if (region != null) {
            booleanFilter.add(new FilterClause(searchUtils.buildRegionFilter(region), Occur.MUST));
        }

        return booleanFilter;
    }

    private PaginatedQueryResult<Document> emptySearchResult() {
        return new PaginatedQueryResult<Document>(new PageConfig(1, 1), Collections.<Document>emptyList(), 0);
    }
}
