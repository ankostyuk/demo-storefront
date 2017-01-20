package ru.nullpointer.storefront.web.secured.manager.catalog.structure.item;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
public class ShiftItemController {

    private Logger logger = LoggerFactory.getLogger(ShiftItemController.class);

    @Resource
    private CatalogService catalogService;

    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    private CatalogItem getItem(Integer itemId) {
        CatalogItem item = catalogService.getItemById(itemId);
        if (item == null) {
            logger.debug("### Элемент каталога с id={} не найден", item);
            throw new NotFoundException();
        }
        return item;
    }

    public void buildModel(Integer itemId, boolean shiftUp, ModelMap model) {
        CatalogItem item = getItem(itemId);
        ItemProperties itemProperties = catalogStructureHelper.buildItemProperties(item, true);
        model.addAttribute("itemProperties", itemProperties);
        model.addAttribute("shiftUp", shiftUp);
    }

    @RequestMapping(value = "/secured/manager/catalog/structure/item/shift/up/{id}", method = RequestMethod.GET)
    public String handleShiftUpGet(@PathVariable("id") Integer itemId, ModelMap model) {
        logger.debug("### handleShiftUpGet");
        buildModel(itemId, true, model);
        return "secured/manager/catalog/structure/item/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/structure/item/shift/down/{id}", method = RequestMethod.GET)
    public String handleShiftDownGet(@PathVariable("id") Integer itemId, ModelMap model) {
        logger.debug("### handleShiftDownGet");
        buildModel(itemId, false, model);
        return "secured/manager/catalog/structure/item/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/structure/item/shift/up/{id}", method = RequestMethod.POST)
    public String handleShiftUpPost(@PathVariable("id") Integer itemId) throws CatalogItemException {
        logger.debug("### handleShiftUpPost");
        shiftItem(itemId, true);
        return "redirect:/secured/manager/catalog/structure";
    }

    @RequestMapping(value = "/secured/manager/catalog/structure/item/shift/down/{id}", method = RequestMethod.POST)
    public String handleShiftDownPost(@PathVariable("id") Integer itemId) throws CatalogItemException {
        logger.debug("### handleShiftDownPost");
        shiftItem(itemId, false);
        return "redirect:/secured/manager/catalog/structure";
    }

    private void shiftItem(Integer itemId, boolean shiftUp) throws CatalogItemException {
        CatalogItem item = getItem(itemId);
        catalogService.shiftItem(item.getId(), shiftUp);
    }
}
