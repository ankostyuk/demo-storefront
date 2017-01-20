package ru.nullpointer.storefront.web.secured.manager.catalog.structure.section;

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
import ru.nullpointer.storefront.web.ui.catalog.SectionProperties;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/structure/section/delete/{id}")
public class DeleteSectionController {

    private Logger logger = LoggerFactory.getLogger(DeleteSectionController.class);

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

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer sectionId, ModelMap model) {
        CatalogItem sectionItem = getSectionItem(sectionId);
        SectionProperties sectionProperties = catalogStructureHelper.buildSectionProperties(sectionItem, true);
        model.addAttribute("sectionProperties", sectionProperties);
        return "secured/manager/catalog/structure/section/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer sectionId) throws CatalogItemException {
        CatalogItem sectionItem = getSectionItem(sectionId);
        catalogService.deleteSection(sectionItem.getId());
        return "redirect:/secured/manager/catalog/structure";
    }
}
