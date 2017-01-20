package ru.nullpointer.storefront.web.secured.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Country;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.support.AliasUtils;
import ru.nullpointer.storefront.domain.support.CompanyCatalogShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.CountryService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.ui.Link;
import ru.nullpointer.storefront.web.ui.TreeNode;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
public class OffersController {

    private Logger logger = LoggerFactory.getLogger(OffersController.class);
    //
    private static final int DEFAULT_PAGE_SIZE = 10;
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private OfferService offerService;
    @Resource
    private OfferHelper offerHelper;
    @Resource
    private UnitService unitService;
    @Resource
    private CountryService countryService;
    @Resource
    private MessageSource messageSource;
    @Resource
    private CurrencyConfig currencyConfig;

    @RequestMapping(value = "/secured/company/offers", method = RequestMethod.GET)
    public String handleGetAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "show", required = false) String show,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {
        return handleGet(null, page, show, sort, cat, model);
    }

    @RequestMapping(value = "/secured/company/offers/{id}", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable(value = "id") Integer id,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "show", required = false) String show,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "cat", required = false) String cat,
            ModelMap model) {

        PageConfig pageConfig = new PageConfig(page, DEFAULT_PAGE_SIZE);
        CompanyOfferSorting sorting = AliasUtils.fromAlias(sort, CompanyOfferSorting.DATE_EDITED_DESCENDING);
        CompanyOfferShowing showing = AliasUtils.fromAlias(show, CompanyOfferShowing.ALL);
        CompanyCatalogShowing catalog = AliasUtils.fromAlias(cat, CompanyCatalogShowing.MY);

        logger.debug("pageConfig: {}, showing: {}, sorting: {}, catalog: {}", new Object[]{pageConfig, showing, sorting, catalog});

        CatalogItem currentItem = null;
        if (id != null) {
            currentItem = catalogService.getItemById(id);
        }

        if (currentItem != null) {
            model.addAttribute("path", catalogService.getPath(currentItem.getId()));
        }

        model.addAttribute("showing", showing);
        model.addAttribute("sorting", sorting);
        model.addAttribute("currentItem", currentItem);

        initCatalogTree(id, catalog, showing, sorting, model);

        Map<Integer, Category> subCategoriesMap = getSubCategoriesMap(currentItem);
        if (subCategoriesMap.isEmpty()) {
            // в этом разделе нет категории
            model.addAttribute("noSubCategories", true);
            return "secured/company/offers_empty";
        }
        
        Set<Integer> categoryIdSet = new HashSet<Integer>(subCategoriesMap.keySet());
        logger.debug("categoryIdSet: {}", categoryIdSet);

        PaginatedQueryResult<Offer> queryResult = offerService.getCompanyOffers(categoryIdSet, pageConfig, showing, sorting);

        logger.debug("queryResult: {}", queryResult);

        if (queryResult.getTotal() == 0) {
            // в этом разделе/категории нет предложений
            return "secured/company/offers_empty";
        }

        model.addAttribute("queryResult", queryResult);
        model.addAttribute("showingCountMap", getShowingCountMap(showing, categoryIdSet));

        Set<Integer> resultCategoryIdSet = new HashSet<Integer>();
        for (Offer offer : queryResult.getList()) {
            resultCategoryIdSet.add(offer.getCategoryId());
        }

        Map<Integer, List<CatalogItem>> pathMap = null;
        // если больше одной категории - отображаем путь
        if (categoryIdSet.size() > 1) {
            pathMap = catalogService.getCategoryPathMap(resultCategoryIdSet);
        }
        model.addAttribute("pathMap", pathMap);
        logger.debug("pathMap: {}", pathMap);

        Map<Integer, Unit> unitMap = getUnitMap(resultCategoryIdSet, subCategoriesMap);
        model.addAttribute("unitMap", unitMap);
        logger.debug("unitMap: {}", unitMap);

        model.addAttribute("originCountryMap", getOriginCountryMap(queryResult.getList()));
        model.addAttribute("defaultCurrency", currencyConfig.getDefaultCurrency());

        return "secured/company/offers";
    }

    private Map<Integer, Category> getSubCategoriesMap(CatalogItem catalogItem) {
        Map<Integer, Category> result;
        if (catalogItem != null) {
            // проверить на наличие категории
            if (catalogItem.getType() == CatalogItem.Type.CATEGORY) {
                Category category = catalogService.getCategoryById(catalogItem.getId());
                result = new HashMap<Integer, Category>(1);
                result.put(category.getId(), category);
            } else {
                // нет такой категории -- получить список подкатегорий раздела
                result = catalogService.getSubCategoriesMap(catalogItem.getId());
            }
        } else {
            // все категории каталога
            result = catalogService.getSubCategoriesMap(null);
        }
        return result;
    }

    /**
     * Возвращает карту единиц измерения. Ключем карты является <code>id</code> категории
     * @param categoryIdSet набор id категорий для которых требуется построить карту
     * @param subCategoriesMap
     * @return
     */
    private Map<Integer, Unit> getUnitMap(Set<Integer> categoryIdSet, Map<Integer, Category> subCategoriesMap) {
        // получить набор id единиц измерения
        Set<Integer> unitIdSet = new HashSet<Integer>();
        for (Integer categoryId : categoryIdSet) {
            Category category = subCategoriesMap.get(categoryId);
            unitIdSet.add(category.getUnitId());
        }

        // получить карту единиц измерения
        Map<Integer, Unit> unitMap = unitService.getUnitMap(unitIdSet);

        // построить результирующую карту
        Map<Integer, Unit> result = new HashMap<Integer, Unit>(categoryIdSet.size());
        for (Integer categoryId : categoryIdSet) {
            Category category = subCategoriesMap.get(categoryId);
            result.put(categoryId, unitMap.get(category.getUnitId()));
        }
        return result;
    }

    private void initCatalogTree(Integer id,
            CompanyCatalogShowing catalog,
            CompanyOfferShowing showing,
            CompanyOfferSorting sorting,
            ModelMap model) {

        List<CatalogItem> itemList = catalogService.getSubTree(null);
        Map<Integer, Integer> offerCountMap = offerService.getCompanyOfferCountMap();

        int totalOfferCount = 0;
        for (Integer c : offerCountMap.values()) {
            totalOfferCount += c;
        }

        if (totalOfferCount == 0) {
            catalog = CompanyCatalogShowing.ALL;
        }

        List<TreeNode> result = new ArrayList<TreeNode>();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("show", showing.getAlias());
        paramMap.put("sort", sorting.getAlias());
        paramMap.put("cat", catalog.getAlias());

        TreeNode.Builder b = new TreeNode.Builder()//
                .setName(messageSource.getMessage("ui.catalog.offer.all", null, null))//
                .setCurrent(id == null)//
                .setLink(new Link("/secured/company/offers", paramMap))//
                .addData("info", totalOfferCount);
        result.add(b.build());

        offerCountMap = offerHelper.calculateCountMap(itemList, offerCountMap);

        for (CatalogItem item : itemList) {
            if (catalog == CompanyCatalogShowing.MY) {
                Integer count = offerCountMap.get(item.getId());
                if (count == null) {
                    continue;
                }
            } else {
                // ничего не делать
            }
            result.add(buildCatalogTreeNode(item, id, offerCountMap, catalog, showing, sorting));
        }

        model.addAttribute("totalOfferCount", totalOfferCount);
        model.addAttribute("catalog", catalog);
        model.addAttribute("catalogTree", result);
    }

    private TreeNode buildCatalogTreeNode(
            CatalogItem item, Integer currentId,
            Map<Integer, Integer> countMap,
            CompanyCatalogShowing catalog,
            CompanyOfferShowing showing,
            CompanyOfferSorting sorting) {

        final String ITEM_URL = "/secured/company/offers/{id}";

        int itemLevel = catalogService.getItemLevel(item);

        TreeNode.Builder b = new TreeNode.Builder()//
                .setName(item.getName())//
                .setLevel(itemLevel)//
                .setDisabled(!item.getActive())//
                .setCurrent(item.getId().equals(currentId))//
                .addData("id", item.getId());

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", item.getId());
        paramMap.put("cat", catalog.getAlias());
        paramMap.put("show", showing.getAlias());
        paramMap.put("sort", sorting.getAlias());

        b.setLink(new Link(ITEM_URL, paramMap));

        Integer offerCount = countMap.get(item.getId());
        if (offerCount != null) {
            b.addData("info", offerCount);
        }

        if (!item.getActive()) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                b.setDescription(messageSource.getMessage("ui.catalog.structure.category.inactive", null, null));
            } else {
                b.setDescription(messageSource.getMessage("ui.catalog.structure.section.inactive", null, null));
            }
        }
        return b.build();
    }

    private Map<String, Country> getOriginCountryMap(List<Offer> offerList) {
        Set<String> countryAlpha2Set = new HashSet<String>();

        for (Offer offer : offerList) {
            countryAlpha2Set.add(offer.getOriginCountry());
        }

        return countryService.getCountryMap(countryAlpha2Set);
    }

    private Map<String, Integer> getShowingCountMap(CompanyOfferShowing showing, Set<Integer> categoryIdSet) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (CompanyOfferShowing s : CompanyOfferShowing.values()) {
            if (s != showing) {
                result.put(s.toString(), offerService.getCompanyOfferCount(categoryIdSet, s));
            }
        }
        return result;
    }
}
