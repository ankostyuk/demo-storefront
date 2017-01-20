package ru.nullpointer.storefront.web.secured.manager.catalog.unit;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.service.UnitService;

/**
 * 
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Controller
public class CatalogUnitController {

    @Resource
    private UnitService unitService;

    @RequestMapping(value = "/secured/manager/catalog/unit", method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        List<Unit> unitList = unitService.getAllUnits();
        model.addAttribute("unitList", unitList);
    }

    @ResponseBody
    @RequestMapping(value = "/secured/manager/catalog/unit/suggest", method = RequestMethod.GET)
    public List<Unit> handleSuggest(@RequestParam("q") String query) {
        return unitService.findUnitListByName(query);
    }
}

