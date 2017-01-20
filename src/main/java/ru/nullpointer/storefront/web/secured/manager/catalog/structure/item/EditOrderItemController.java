package ru.nullpointer.storefront.web.secured.manager.catalog.structure.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.web.exception.NotFoundException;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/item/edit/order/{id}")
public class EditOrderItemController {

    private Logger logger = LoggerFactory.getLogger(EditOrderItemController.class);

    @Resource
    private CatalogService catalogService;

    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    private CatalogItem getItem(Integer itemId) {
        CatalogItem catalogItem = catalogService.getItemById(itemId);
        if (catalogItem == null) {
            logger.debug("### Элемент каталога с id={} не найден", itemId);
            throw new NotFoundException();
        }
        return catalogItem;
    }

    @ModelAttribute("itemProperties")
    public ItemProperties getItemProperties(@PathVariable("id") Integer itemId) {
        CatalogItem catalogItem = getItem(itemId);
        ItemProperties itemProperties = catalogStructureHelper.buildItemProperties(catalogItem, true);
        return itemProperties;
    }

    @ModelAttribute("afterItemList")
    public List<ItemProperties> getAfterItemList(@PathVariable("id") Integer itemId) {
        CatalogItem catalogItem = getItem(itemId);

        List<ItemProperties> afterItemList = new ArrayList<ItemProperties>();

        CatalogItem parentItem = catalogService.getParentItem(catalogItem.getId());
        List<CatalogItem> childrenList = catalogService.getChildrenList(parentItem != null ? parentItem.getId() : null);

        for (CatalogItem item : childrenList) {
            ItemProperties afterItemProperties = catalogStructureHelper.buildItemProperties(item, false);

            if (!catalogService.isEquals(item, catalogItem)) {
                afterItemProperties.setCanChoose(true);
            } else {
                afterItemProperties.setCanChoose(false);
            }

            afterItemList.add(afterItemProperties);
        }

        // for last
        ItemProperties childrenLastProperties = catalogStructureHelper.buildChildrenLastItemProperties();
        childrenLastProperties.setCanChoose(true);
        afterItemList.add(childrenLastProperties);

        return afterItemList;
    }

    @InitBinder("itemProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{"afterItemId"});
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/structure/item/edit/order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("itemProperties") ItemProperties itemProperties, BindingResult result) throws CatalogItemException {

        catalogService.changeAfterItem(itemProperties.getItem().getId(), itemProperties.getAfterItemId());

        return "redirect:/secured/manager/catalog/structure";
    }

    // TODO Формирование порядка отображения в списке - to SRS (3.1.12.2.
    // Ведение раздела)
}
