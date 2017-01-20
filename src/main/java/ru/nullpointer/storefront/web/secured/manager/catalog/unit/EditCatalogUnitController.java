package ru.nullpointer.storefront.web.secured.manager.catalog.unit;

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
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 * 
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/unit/edit/{id}")
public class EditCatalogUnitController {

    private Logger logger = LoggerFactory.getLogger(EditCatalogUnitController.class);

    @Resource
    private UnitService unitService;

    @Autowired
    private BeanValidator validator;

    public Unit getUnit(Integer unitId) {
        Unit unit = unitService.getUnitById(unitId);
        if (unit == null) {
            logger.debug("### Единица измерения категорий с id={} не найдена", unitId);
            throw new NotFoundException();
        }
        return unit;
    }

    @ModelAttribute("unit")
    public Unit getUnitModelAttribute(@PathVariable("id") Integer unitId) {
        return getUnit(unitId);
    }

    @ModelAttribute("canDelete")
    public Boolean getCanDeleteModelAttribute(@PathVariable("id") Integer unitId) {
        return unitService.canDeleteUnit(getUnit(unitId));
    }

    @InitBinder("unit")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{"name", "abbreviation"});
    }

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/unit/edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@ModelAttribute("unit") Unit unit, BindingResult result) {
        validator.validate(unit, result);
        if (result.hasErrors()) {
            return "secured/manager/catalog/unit/edit";
        }

        unitService.updateUnitInfo(unit);

        return "redirect:/secured/manager/catalog/unit";
    }
}
