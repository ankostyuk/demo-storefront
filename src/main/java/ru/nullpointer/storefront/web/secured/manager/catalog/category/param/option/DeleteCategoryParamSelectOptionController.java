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
@RequestMapping("/secured/manager/catalog/category/param/select/option/delete/{id}")
public class DeleteCategoryParamSelectOptionController {

    private Logger logger = LoggerFactory.getLogger(DeleteCategoryParamSelectOptionController.class);
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

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer optionId, ModelMap model) {
        ParamSelectOption paramSelectOption = getParamSelectOption(optionId);
        model.addAttribute("paramSelectOption", paramSelectOption);
        model.addAttribute("canDelete", paramService.canDeleteParamSelectOption(paramSelectOption));
        return "secured/manager/catalog/category/param/select/option/delete";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(@PathVariable("id") Integer optionId) {
        ParamSelectOption paramSelectOption = getParamSelectOption(optionId);

        paramService.deleteParamSelectOption(paramSelectOption);

        // TODO якорь на группу параметров?
        return new StringBuilder("redirect:/secured/manager/catalog/category/param/edit/")//
                .append(paramSelectOption.getParamId())//
                .toString();
    }
}
