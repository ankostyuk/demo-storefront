package ru.nullpointer.storefront.web.secured.manager.catalog.structure.section;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.secured.manager.catalog.structure.CatalogStructureHelper;
import ru.nullpointer.storefront.web.ui.catalog.ItemProperties;
import ru.nullpointer.storefront.web.ui.catalog.SectionProperties;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/section/add")
public class AddSectionController {

    private Logger logger = LoggerFactory.getLogger(AddSectionController.class);
    @Resource
    private CatalogService catalogService;
    @Autowired
    private CatalogStructureHelper catalogStructureHelper;
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("sectionProperties")
    public SectionProperties getSectionProperties() {
        SectionProperties sectionProperties = catalogStructureHelper.buildNewSectionProperties();
        return sectionProperties;
    }

    @ModelAttribute("parentItemList")
    public List<ItemProperties> getParentItemList() {
        List<ItemProperties> parentItemList = new ArrayList<ItemProperties>();

        // for root
        ItemProperties catalogRootProperties = catalogStructureHelper.buildCatalogRootItemProperties();
        catalogRootProperties.setCanChoose(true);
        parentItemList.add(catalogRootProperties);

        List<CatalogItem> subTree = catalogService.getSubTree(null);
        for (CatalogItem item : subTree) {
            ItemProperties parentItemProperties = catalogStructureHelper.buildItemProperties(item, false);

            if (catalogService.isSectionItem(item)) {
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
        binder.setAllowedFields(
                "item.name",
                "item.theme",
                //"item.active",
                "parentItemId");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(
            @RequestParam(value = "parentId", required = false) Integer parentId,
            @ModelAttribute("sectionProperties") SectionProperties sectionProperties) {
        if (parentId != null) {
            CatalogItem parentSection = catalogService.getSectionItemById(parentId);
            if (parentSection == null) {
                return "redirect:/secured/manager/catalog/structure";
            }
        }
        sectionProperties.setParentItemId(parentId);
        return "secured/manager/catalog/structure/section/add";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("sectionProperties") SectionProperties sectionProperties,
            BindingResult result, ModelMap model) throws CatalogItemException {
        validator.validate(sectionProperties, result);
        if (result.hasErrors()) {
            return "secured/manager/catalog/structure/section/add";
        }

        // TODO try catch ?
        catalogService.addSection(sectionProperties.getItem(), sectionProperties.getParentItemId());

        model.clear();
        return "redirect:/secured/manager/catalog/structure";
    }
}
