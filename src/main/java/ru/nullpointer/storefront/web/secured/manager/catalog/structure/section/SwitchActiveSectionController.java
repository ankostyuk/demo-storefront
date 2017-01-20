package ru.nullpointer.storefront.web.secured.manager.catalog.structure.section;

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
@RequestMapping("/secured/manager/catalog/structure/section/switch/active/{id}")
public class SwitchActiveSectionController {

    private Logger logger = LoggerFactory.getLogger(SwitchActiveSectionController.class);

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
        logger.debug("### @ModelAttribute: sectionProperties={}", sectionProperties);
        return sectionProperties;
    }

    @InitBinder("sectionProperties")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
            "item.active"
        });
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        logger.debug("### handleGet");
        return "secured/manager/catalog/structure/section/switch/active";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
                @ModelAttribute("sectionProperties") SectionProperties sectionProperties,
                BindingResult result
            ) {
        logger.debug("### handlePost: sectionProperties={}", sectionProperties);

        CatalogItem sectionItem = sectionProperties.getItem();
        sectionItem.setActive(!sectionItem.getActive());
        catalogService.updateSectionTreeActive(sectionItem);
        
        return "redirect:/secured/manager/catalog/structure";
    }
}
