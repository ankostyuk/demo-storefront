package ru.nullpointer.storefront.web.secured.manager.moderator.offer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.DateWindowConfig;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.CompanyService;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.ui.Link;
import ru.nullpointer.storefront.web.ui.TreeNode;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ModeratorOfferController {

    private static final int DEFAULT_WINDOW_LIMIT = 10;
    //
    @Resource
    private CatalogService catalogService;
    @Resource
    private OfferService offerService;
    @Resource
    private UnitService unitService;
    @Resource
    private CompanyService companyService;
    @Resource
    private MessageSource messageSource;

    @RequestMapping(value = "/secured/manager/moderator/offer", method = RequestMethod.GET)
    public String handleGetAll(ModelMap model) {

        buildCatalogTree(null, null, model);

        return "secured/manager/moderator/offer";
    }

    @RequestMapping(value = "/secured/manager/moderator/offer/{id}", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable(value = "id") Integer categoryId,
            @RequestParam(value = "startDate", required = false) Long startDateLong,
            @RequestParam(value = "startId", required = false) Integer startId,
            @RequestParam(value = "forward", defaultValue = "true") Boolean forward,
            @RequestParam(value = "all", defaultValue = "false") Boolean all,
            ModelMap model) {

        Category category = getCategory(categoryId);

        model.addAttribute("category", category);
        model.addAttribute("unit", unitService.getUnitById(category.getUnitId()));
        buildCatalogTree(categoryId, all, model);

        Date startDate = startDateLong != null ? new Date(startDateLong) : null;

        DateWindowConfig windowConfig = new DateWindowConfig(startDate, startId, DEFAULT_WINDOW_LIMIT, forward);
        List<Offer> offerList = offerService.getModeratorOfferList(categoryId, windowConfig, !all);

        model.addAttribute("offerList", offerList);
        model.addAttribute("companyMap", getCompanyMap(offerList));

        return "secured/manager/moderator/offer";
    }

    private Map<Integer, Company> getCompanyMap(List<Offer> offerList) {
        Set<Integer> companyIdSet = new HashSet<Integer>();
        for (Offer offer : offerList) {
            companyIdSet.add(offer.getCompanyId());
        }
        return companyService.getCompanyMap(companyIdSet);
    }

    private Category getCategory(Integer id) {
        Category category = catalogService.getCategoryById(id);
        if (category == null) {
            throw new NotFoundException();
        }
        return category;
    }

    private void buildCatalogTree(Integer currentItemId, Boolean all, ModelMap model) {
        final String URL_PREFIX = "/secured/manager/moderator/offer";
        final String ITEM_URL = URL_PREFIX + "/{id}";

        List<CatalogItem> itemList = catalogService.getSubTree(null);
        Map<Integer, Integer> pendingCountMap = offerService.getModeratorPendingOfferCountMap();

        int totalPendingCount = 0;
        for (Integer c : pendingCountMap.values()) {
            totalPendingCount += c;
        }

        List<List<CatalogItem>> pendingPathList = new ArrayList<List<CatalogItem>>(pendingCountMap.size());

        List<TreeNode> catalogTree = new ArrayList<TreeNode>(itemList.size());

        TreeNode.Builder b = new TreeNode.Builder()//
                .setName(messageSource.getMessage("ui.catalog.structure.item.all", null, null))//
                .setCurrent(currentItemId == null)//
                .addData("info", totalPendingCount)//
                .setLink(new Link(URL_PREFIX));
        catalogTree.add(b.build());

        for (CatalogItem item : itemList) {
            Integer itemId = item.getId();

            b = new TreeNode.Builder()//
                    .setName(item.getName())//
                    .setLevel(catalogService.getItemLevel(item))//
                    .setDisabled(!item.getActive())//
                    .setCurrent(itemId.equals(currentItemId));

            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("id", itemId);
                if (all != null && all) {
                    paramMap.put("all", all);
                }
                b.setLink(new Link(ITEM_URL, paramMap));

                Integer offerCount = pendingCountMap.get(itemId);
                if (offerCount != null) {
                    b.addData("info", offerCount);
                    pendingPathList.add(catalogService.getPath(itemId));
                }

                if (!item.getActive()) {
                    b.setDescription(messageSource.getMessage("ui.catalog.structure.category.inactive", null, null));
                }
            } else {
                if (!item.getActive()) {
                    b.setDescription(messageSource.getMessage("ui.catalog.structure.section.inactive", null, null));
                }
            }

            catalogTree.add(b.build());
        }

        model.addAttribute("catalogTree", catalogTree);
        model.addAttribute("pendingPathList", pendingPathList);
        model.addAttribute("pendingCountMap", pendingCountMap);
    }
}
