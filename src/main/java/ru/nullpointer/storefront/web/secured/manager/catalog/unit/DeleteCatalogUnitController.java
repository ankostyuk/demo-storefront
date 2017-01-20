package ru.nullpointer.storefront.web.secured.manager.catalog.unit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/unit/delete/{id}")
public class DeleteCatalogUnitController {

    private Logger logger = LoggerFactory.getLogger(DeleteCatalogUnitController.class);

    @Resource
    private UnitService unitService;

    private Unit getUnit(Integer unitId) {
        Unit unit = unitService.getUnitById(unitId);
        if (unit == null) {
            logger.debug("### Единица измерения категорий с id={} не найдена", unitId);
            throw new NotFoundException();
        }
        return unit;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer unitId, ModelMap model) {
        Unit unit = getUnit(unitId);
        model.addAttribute("unit", unit);
        model.addAttribute("canDelete", unitService.canDeleteUnit(unit));
        return "secured/manager/catalog/unit/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer unitId) {
        Unit unit = getUnit(unitId);

        unitService.deleteUnit(unit);

        return "redirect:/secured/manager/catalog/unit";
    }
}
