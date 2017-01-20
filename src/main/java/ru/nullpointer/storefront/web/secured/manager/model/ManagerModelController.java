package ru.nullpointer.storefront.web.secured.manager.model;

import java.util.ArrayList;
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
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.ModelService;
import ru.nullpointer.storefront.web.ui.Link;
import ru.nullpointer.storefront.web.ui.TreeNode;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class ManagerModelController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    //
    @Resource
    private ModelService modelService;
    @Resource
    private CatalogService catalogService;
    @Resource
    private ModelHelper modelHelper;
    //
    @Resource
    private MessageSource messageSource;

    @RequestMapping(value = "/secured/manager/model", method = RequestMethod.GET)
    public String handleGetAll(ModelMap model) {
        model.addAttribute("catalogTree", buildCatalogTree(null));
        return "secured/manager/model";
    }

    @RequestMapping(value = "/secured/manager/model/{id}", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer categoryId,
            @RequestParam(value = "page", required = false) Integer page,
            ModelMap model) {

        Category category = modelHelper.getCategory(categoryId);

        PageConfig pageConfig = new PageConfig(page, DEFAULT_PAGE_SIZE);

        PaginatedQueryResult<Model> queryResult = modelService.getModelList(category.getId(), pageConfig);

        model.addAttribute("queryResult", queryResult);
        model.addAttribute("category", category);
        model.addAttribute("path", catalogService.getPath(categoryId));
        model.addAttribute("catalogTree", buildCatalogTree(categoryId));

        return "secured/manager/model";
    }

    private List<TreeNode> buildCatalogTree(Integer categoryId) {

        final String URL_PREFIX = "/secured/manager/model";
        final String CATEGORY_URL = URL_PREFIX + "/{catId}";

        List<CatalogItem> itemList = catalogService.getSubTree(null);

        Set<Integer> categoryIdSet = getCategoryIdSet(itemList);
        Map<Integer, Category> categoryMap = catalogService.getCategoryMap(categoryIdSet);
        Map<Integer, Integer> modelCountMap = modelService.getModelCountMap(categoryIdSet);

        List<TreeNode> result = new ArrayList<TreeNode>(itemList.size());

        TreeNode.Builder b = new TreeNode.Builder()//
                .setName(messageSource.getMessage("ui.catalog.structure.item.all", null, null))//
                .setCurrent(categoryId == null)//
                .setLink(new Link(URL_PREFIX));
        result.add(b.build());

        for (CatalogItem item : itemList) {
            Integer itemId = item.getId();

            b = new TreeNode.Builder()//
                    .setName(item.getName())//
                    .setLevel(catalogService.getItemLevel(item))//
                    .setDisabled(!item.getActive())//
                    .setCurrent(itemId.equals(categoryId));

            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Category category = categoryMap.get(itemId);
                if (category.isParametrized()) {
                    b.setLink(new Link(CATEGORY_URL, "catId", itemId));
                }

                Integer modelCount = modelCountMap.get(itemId);
                if (modelCount != null) {
                    b.addData("info", modelCount);
                }

                if (item.getActive()) {
                    if (!category.isParametrized()) {
                        b.setDescription(messageSource.getMessage("ui.catalog.structure.category.notparametrized", null, null));
                    }
                } else {
                    b.setDescription(messageSource.getMessage("ui.catalog.structure.category.inactive", null, null));
                }
            } else {
                if (!item.getActive()) {
                    b.setDescription(messageSource.getMessage("ui.catalog.structure.section.inactive", null, null));
                }
            }

            result.add(b.build());
        }
        return result;
    }

    private Set<Integer> getCategoryIdSet(List<CatalogItem> itemList) {
        Set<Integer> categoryIdSet = new HashSet<Integer>();
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                categoryIdSet.add(item.getId());
            }
        }
        return categoryIdSet;
    }
}
