package ru.nullpointer.storefront.web.secured.manager.catalog.category.param.option;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author ankostyuk
 */
@Controller
public class ShiftCategoryParamSelectOptionController {

    private Logger logger = LoggerFactory.getLogger(ShiftCategoryParamSelectOptionController.class);
    //
    @Resource
    private ParamService paramService;

    private ParamSelectOption getParamSelectOption(Integer optionId) {
        ParamSelectOption paramSelectOption = paramService.getParamSelectOptionById(optionId);
        if (paramSelectOption == null) {
            throw new NotFoundException("Вариант выбора с id " + optionId + " не найден");
        }
        return paramSelectOption;
    }

    public void initModel(Integer optionId, boolean shiftUp, ModelMap model) {
        ParamSelectOption paramSelectOption = getParamSelectOption(optionId);
        model.addAttribute("paramSelectOption", paramSelectOption);
        model.addAttribute("shiftUp", shiftUp);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/option/shift/up/{id}", method = RequestMethod.GET)
    public String handleShiftUpGet(@PathVariable("id") Integer optionId, ModelMap model) {
        initModel(optionId, true, model);
        return "secured/manager/catalog/category/param/select/option/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/option/shift/down/{id}", method = RequestMethod.GET)
    public String handleShiftDownGet(@PathVariable("id") Integer optionId, ModelMap model) {
        initModel(optionId, false, model);
        return "secured/manager/catalog/category/param/select/option/shift";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/option/shift/up/{id}", method = RequestMethod.POST)
    public String handleShiftUpPost(@PathVariable("id") Integer optionId) {
        return shiftParamSelectOption(optionId, true);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/option/shift/down/{id}", method = RequestMethod.POST)
    public String handleShiftDownPost(@PathVariable("id") Integer optionId) {
        return shiftParamSelectOption(optionId, false);
    }

    private String shiftParamSelectOption(Integer optionId, boolean shiftUp) {
        ParamSelectOption paramSelectOption = getParamSelectOption(optionId);

        paramSelectOption.setOrdinal(shiftUp ? paramSelectOption.getOrdinal() - 1 : paramSelectOption.getOrdinal() + 1);
        paramService.updateParamSelectOptionOrder(paramSelectOption);

        // TODO якорь на группу параметров?
        return new StringBuilder("redirect:/secured/manager/catalog/category/param/edit/")//
                .append(paramSelectOption.getParamId())//
                .toString();
    }
}
