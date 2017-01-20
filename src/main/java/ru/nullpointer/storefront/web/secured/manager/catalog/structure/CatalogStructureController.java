package ru.nullpointer.storefront.web.secured.manager.catalog.structure;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure")
public class CatalogStructureController {

    @Resource
    private CatalogService catalogService;

    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        List<CatalogItem> subTree = catalogService.getSubTree(null);
        List<ItemProperties> itemList = new ArrayList<ItemProperties>();

        for (CatalogItem item : subTree) {
            itemList.add(catalogStructureHelper.buildItemProperties(item, true));
        }

        model.addAttribute("itemList", itemList);
    }
}
