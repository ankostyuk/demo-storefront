package ru.nullpointer.storefront.web.secured.manager.catalog.structure.section;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
import ru.nullpointer.storefront.web.ui.catalog.SectionProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/section/edit/section/{id}")
public class EditParentSectionController {

    private Logger logger = LoggerFactory.getLogger(EditParentSectionController.class);
    @Resource
    private CatalogService catalogService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;

    private CatalogItem getSectionItem(Integer sectionId) {
        CatalogItem sectionItem = catalogService.getSectionItemById(sectionId);
        if (sectionItem == null) {
            logger.debug("### Раздел каталога с id={} не найден", sectionId);
            throw new NotFoundException();
        }
        return sectionItem;
    }

    @ModelAttribute("sectionProperties")
    public SectionProperties getSectionProperties(@PathVariable("id") Integer sectionId) {
        CatalogItem sectionItem = getSectionItem(sectionId);
        SectionProperties sectionProperties = catalogStructureHelper.buildSectionProperties(sectionItem, true);
        return sectionProperties;
    }

    @ModelAttribute("parentItemList")
    public List<ItemProperties> getParentItemList(@PathVariable("id") Integer sectionId) {
        CatalogItem sectionItem = getSectionItem(sectionId);

        List<ItemProperties> parentItemList = new ArrayList<ItemProperties>();

        // for root
        ItemProperties catalogRootProperties = catalogStructureHelper.buildCatalogRootItemProperties();
        catalogRootProperties.setCanChoose(true);
        parentItemList.add(catalogRootProperties);

        List<CatalogItem> subTree = catalogService.getSubTree(null);
        for (CatalogItem item : subTree) {
            ItemProperties parentItemProperties = catalogStructureHelper.buildItemProperties(item, false);

            if (catalogService.isSectionItem(item) && !catalogService.isEquals(item, sectionItem) && !catalogService.isChildItem(item, sectionItem)) {
                //
                parentItemProperties.setCanChoose(true);
            } else {
                parentItemProperties.setCanChoose(false);
            }
            parentItemProperties.setLevel(catalogService.getItemLevel(item));
            
            parentItemList.add(parentItemProperties);
        }

        return parentItemList;
    }

    @InitBinder("sectionProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
                    "parentItemId"
                });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/structure/section/edit/section";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("sectionProperties") SectionProperties sectionProperties,
            BindingResult result) throws CatalogItemException {

        catalogService.changeParentItem(sectionProperties.getItem(), sectionProperties.getParentItemId());

        return "redirect:/secured/manager/catalog/structure";
    }
}
