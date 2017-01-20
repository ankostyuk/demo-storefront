package ru.nullpointer.storefront.web.secured.company;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Country;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.CompanyCatalogShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.CountryService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.TimeService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.ui.DateEditor;
import ru.nullpointer.storefront.web.ui.DecimalEditor;
import ru.nullpointer.storefront.web.ui.SelectOption;
import ru.nullpointer.storefront.web.ui.TreeNode;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
class OfferHelper {

    private static final int DEFAULT_ACTUAL_PERIOD_MONTHS = 2;
    private static final int MAX_ACTUAL_PERIOD_MONTHS = 6;
    //
    private static Logger logger = LoggerFactory.getLogger(OfferHelper.class);
    //
    @Resource
    private OfferService offerService;
    @Resource
    private CountryService countryService;
    @Resource
    private TimeService timeService;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private CatalogService catalogService;

    static String buildRedirectUrl(Integer itemId, CompanyOfferSorting sorting) {
        Assert.notNull(sorting);

        StringBuilder sb = new StringBuilder("redirect:/secured/company/offers");
        if (itemId != null) {
            sb.append("/");
            sb.append(itemId);
        }
        if (sorting != CompanyOfferSorting.DATE_CREATED_DESCENDING) {
            sb.append("?");
            sb.append("sort=");
            sb.append(sorting.getAlias());
        }
        return sb.toString();
    }

    static void logImageFile(MultipartFile imageFile, Logger logger) {
        if (imageFile == null) {
            logger.debug("offerImage is null");
        } else {
            logger.debug("offerImage name: {}, original name: {}, content type: {}, size: {}",
                    new Object[]{
                        imageFile.getName(),
                        imageFile.getOriginalFilename(),
                        imageFile.getContentType(),
                        imageFile.getSize()
                    });
        }
    }

    List<SelectOption<String>> getCurrencyList() {
        List<SelectOption<String>> list = new ArrayList<SelectOption<String>>();
        for (String s : currencyConfig.getCurrencyList()) {
            list.add(new SelectOption<String>(s, s));
        }
        return list;
    }

    List<SelectOption<String>> getCountryList() {
        List<Country> countryList = countryService.getCountryList();
        List<SelectOption<String>> list = new ArrayList<SelectOption<String>>(countryList.size());

        for (Country country : countryList) {
            list.add(new SelectOption<String>(country.getAlpha2(), country.getName()));
        }

        return list;
    }

    Date getDefaultActualDate() {
        return DateUtils.addMonths(timeService.now(), DEFAULT_ACTUAL_PERIOD_MONTHS);
    }

    void validateActualDate(Offer offer, BindingResult result) {
        if (result.hasFieldErrors("actualDate")) {
            return;
        }

        Assert.notNull(offer.getActualDate());

        if (!offer.getActive()) {
            return;
        }

        Date now = timeService.now();
        Date max = DateUtils.addMonths(now, MAX_ACTUAL_PERIOD_MONTHS);

        if (offer.getActualDate().compareTo(now) <= 0) {
            result.rejectValue("actualDate", "validation.Offer.actualDate.future");
        } else if (offer.getActualDate().compareTo(max) >= 0) {
            String maxDate = DateEditor.getDateFormat().format(max);
            result.rejectValue("actualDate", "validation.Offer.actualDate.max", new Object[]{maxDate}, null);
        }
    }

    void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(
                "name", "description",
                "brandName", "modelName", "originCountry",
                "price", "currency", "ratio",
                "actualDate", "active", "available", "delivery",
                "brandId", "modelId");
        binder.registerCustomEditor(Date.class, new DateEditor(true));
        binder.registerCustomEditor(BigDecimal.class, "price", new DecimalEditor(BigDecimal.class, true, 2));
        binder.registerCustomEditor(BigDecimal.class, "ratio", new DecimalEditor(BigDecimal.class, true, 4));
    }

    Offer getOffer(Integer offerId) {
        Offer offer = offerService.getCompanyOfferById(offerId);
        if (offer == null) {
            throw new NotFoundException("Предложение с номером  «" + offerId + "» не найдено");
        }
        return offer;
    }

    /**
     * Возвращает дерево кателога
     * @param currentItemId идентификатор активного узла. может быть <code>null</code>
     * @return
     */
    List<TreeNode> buildCatalogTree(Integer currentItemId) {
        List<CatalogItem> itemList = catalogService.getSubTree(null);
        List<TreeNode> result = new ArrayList<TreeNode>(itemList.size());
        for (CatalogItem item : itemList) {
            TreeNode.Builder b = new TreeNode.Builder()//
                    .setName(item.getName())//
                    .setLevel(catalogService.getItemLevel(item))//
                    .setCurrent(item.getId().equals(currentItemId))//
                    .setDisabled(!item.getActive());

            // Добавить id, только если категория
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                b.addData("id", item.getId());
            }
            result.add(b.build());
        }
        return result;
    }

    void initCategorySelectTree(Integer parentId, CompanyCatalogShowing catalog, ModelMap model) {
        if (parentId != null) {
            CatalogItem parentItem = catalogService.getSectionItemById(parentId);
            if (parentItem == null) {
                throw new NotFoundException();
            }
            model.addAttribute("parentItem", parentItem);
            model.addAttribute("path", catalogService.getPath(parentId));
        }

        List<CatalogItem> itemList = catalogService.getSubTree(parentId);
        Map<Integer, Integer> offerCountMap = offerService.getCompanyOfferCountMap();

        int offerCount = 0;
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Integer count = offerCountMap.get(item.getId());
                if (count != null) {
                    offerCount += count;
                }
            }
        }

        if (offerCount == 0) {
            catalog = CompanyCatalogShowing.ALL;
        }
        model.addAttribute("showCatalogTreeFilter", offerCount != 0);

        offerCountMap = calculateCountMap(itemList, offerCountMap);

        List<TreeNode> result = new ArrayList<TreeNode>(itemList.size());
        for (CatalogItem item : itemList) {
            Integer count = offerCountMap.get(item.getId());

            if (catalog == CompanyCatalogShowing.MY && count == null) {
                continue;
            } else {
                // ничего не делать
            }

            TreeNode.Builder b = new TreeNode.Builder()//
                    .setName(item.getName())//
                    .setLevel(catalogService.getItemLevel(item))//
                    .setDisabled(!item.getActive())//
                    .addData("id", item.getId())//
                    .addData("type", item.getType());

            if (count != null) {
                b.addData("info", count);
            }

            result.add(b.build());
        }

        model.addAttribute("catalog", catalog);
        model.addAttribute("catalogTree", result);
    }

    /**
     * Вычисляет количество предложений в непустых категориях и разделах.
     * @param itemList
     * @param offerCountMap
     * @return Карту счетчиков. Ключем карты является идентификатор раздела или категории.
     * Значением - количество предложнний.
     */
    Map<Integer, Integer> calculateCountMap(List<CatalogItem> itemList, Map<Integer, Integer> offerCountMap) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();

        List<CatalogItem> path = new ArrayList<CatalogItem>();
        for (CatalogItem item : itemList) {
            int level = catalogService.getItemLevel(item);

            // поднялись на уровень - удалить родителей из цепочки
            if (level < path.size()) {
                for (int i = 0; i < path.size() - level + 1; i++) {
                    path.remove(path.size() - 1);
                }
            }

            if (item.getType() == CatalogItem.Type.CATEGORY) {
                // Категория
                Integer count = offerCountMap.get(item.getId());
                if (count != null) {
                    // добавить всем родителям
                    for (CatalogItem pathItem : path) {
                        Integer c = result.get(pathItem.getId());
                        result.put(pathItem.getId(), c != null ? c + count : count);
                    }
                    // скопировать счетчик самой категории
                    result.put(item.getId(), count);
                }
            } else {
                // раздел
                // тот же уровень - заменить родителя
                if (level == path.size()) {
                    path.remove(path.size() - 1);
                }
                // добавить в цепочку родителя
                path.add(item);
            }
        }

        logger.debug("result: {}", result);

        return result;
    }
}
