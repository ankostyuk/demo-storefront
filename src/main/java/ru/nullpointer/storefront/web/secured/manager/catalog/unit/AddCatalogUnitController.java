package ru.nullpointer.storefront.web.secured.manager.catalog.unit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author ankostyuk
 */
@Controller
@RequestMapping("/secured/manager/catalog/unit/add")
public class AddCatalogUnitController {

    @Resource
    private UnitService unitService;
    
    @Autowired
    private BeanValidator validator;

    @ModelAttribute("unit")
    public Unit getUnitModelAttribute() {
        return new Unit();
    }

    @InitBinder("unit")
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields(new String[]{
            "name",
            "abbreviation"
        });
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String handleGet() {
        return "secured/manager/catalog/unit/add";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
                @ModelAttribute("unit") Unit unit,
                BindingResult result
            ) {
        validator.validate(unit, result);
        if (result.hasErrors()) { 
            return "secured/manager/catalog/unit/add";
        }
        
        unitService.addUnit(unit);
        
        return "redirect:/secured/manager/catalog/unit";
    }
}
