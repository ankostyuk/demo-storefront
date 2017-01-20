package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.param.BooleanParam;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.domain.param.SelectParam;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.util.StringIndexComparator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Controller
public class AddCategoryParamController {

    private Logger logger = LoggerFactory.getLogger(AddCategoryParamController.class);
    //
    @Resource
    private ParamService paramService;
    @Resource
    private MessageSource messageSource;
    @Autowired
    private CategoryParamHelper categoryParamHelper;

    private ParamGroup getParamGroup(Integer paramGroupId) {
        ParamGroup paramGroup = paramService.getParamGroupById(paramGroupId);
        if (paramGroup == null) {
            throw new NotFoundException("Группа параметров категории с id " + paramGroupId + " не найдена");
        }
        return paramGroup;
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/add/{id}", method = RequestMethod.GET)
    public String handleGet(@PathVariable("id") Integer paramGroupId, ModelMap model) {
        initModel(paramGroupId, model);
        return "secured/manager/catalog/category/param/add";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/boolean/add/{id}", method = RequestMethod.GET)
    public String handleGetBoolean(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model) {

        initModel(paramGroupId, model);

        BooleanParam param = new BooleanParam();
        param.setTrueName(messageSource.getMessage("ui.catalog.category.BooleanParam.trueName.default", null, null));
        param.setFalseName(messageSource.getMessage("ui.catalog.category.BooleanParam.falseName.default", null, null));

        categoryParamHelper.initBooleanBinder(param, model);

        return "secured/manager/catalog/category/param/boolean/add";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/boolean/add/{id}", method = RequestMethod.POST)
    public String handlePostBoolean(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model, WebRequest request) {

        initModel(paramGroupId, model);

        Param param = new BooleanParam();
        WebRequestDataBinder binder = categoryParamHelper.initBooleanBinder(param, model);

        binder.bind(request);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/boolean/add";
        }

        paramService.addParam((ParamGroup) model.get("paramGroup"), param);

        return categoryParamHelper.buildRedirectUrl(model);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/number/add/{id}", method = RequestMethod.GET)
    public String handleGetNumber(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model) {

        initModel(paramGroupId, model);

        NumberParam param = new NumberParam();
        categoryParamHelper.initNumberBinder(param, model);
        categoryParamHelper.initNumberParamUnit(param, model);

        return "secured/manager/catalog/category/param/number/add";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/number/add/{id}", method = RequestMethod.POST)
    public String handlePostNumber(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model, WebRequest request) {

        initModel(paramGroupId, model);

        NumberParam param = new NumberParam();
        WebRequestDataBinder binder = categoryParamHelper.initNumberBinder(param, model);
        binder.bind(request);

        categoryParamHelper.initNumberParamUnit(param, model);

        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/number/add";
        }

        paramService.addParam((ParamGroup) model.get("paramGroup"), param);

        return categoryParamHelper.buildRedirectUrl(model);
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/add/{id}", method = RequestMethod.GET)
    public String handleGetSelect(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model) {

        initModel(paramGroupId, model);

        Param param = new SelectParam();
        categoryParamHelper.initSelectBinder(param, model);

        OptionsValueMap options = new OptionsValueMap();
        options.getValue().put("0", new ParamSelectOption());
        categoryParamHelper.initOptionsBinder(options, model);

        return "secured/manager/catalog/category/param/select/add";
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/select/add/{id}", method = RequestMethod.POST)
    public String handlePostSelect(
            @PathVariable("id") Integer paramGroupId,
            ModelMap model, WebRequest request,
            @RequestParam(value = "continue", required = false) String cont) {

        initModel(paramGroupId, model);

        Param param = new SelectParam();
        WebRequestDataBinder binder = categoryParamHelper.initSelectBinder(param, model);
        binder.bind(request);
        binder.validate();

        OptionsValueMap options = new OptionsValueMap();
        WebRequestDataBinder optionsBinder = categoryParamHelper.initOptionsBinder(options, model);
        optionsBinder.bind(request);
        optionsBinder.validate();

        if (binder.getBindingResult().hasErrors() || optionsBinder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/select/add";
        }

        paramService.addParam((ParamGroup) model.get("paramGroup"), param);
        paramService.addParamSelectOptions(param.getId(), getOptions(model));

        if (cont != null) {
            // вернуться к редактированию
            return "redirect:/secured/manager/catalog/category/param/edit/" + param.getId();
        }
        return categoryParamHelper.buildRedirectUrl(model);
    }

    @SuppressWarnings("unchecked")
    private List<ParamSelectOption> getOptions(ModelMap model) {
        SortedMap<String, Object> sortedMap = new TreeMap<String, Object>(new StringIndexComparator());
        sortedMap.putAll(((OptionsValueMap) model.get("selectOptions")).getValue());

        List<ParamSelectOption> result = new ArrayList<ParamSelectOption>();
        for (String key : sortedMap.keySet()) {
            ParamSelectOption option = (ParamSelectOption) sortedMap.get(key);
            String name = option.getName();
            if (name != null && !name.trim().isEmpty()) {
                result.add(option);
            }
        }
        return result;
    }

    private void initModel(Integer paramGroupId, ModelMap model) {
        ParamGroup paramGroup = getParamGroup(paramGroupId);
        CatalogItem categoryItem = paramService.getCategoryItemByParamGroupId(paramGroupId);

        model.addAttribute("paramGroup", paramGroup);
        model.addAttribute("catalogItem", categoryItem);
        model.addAttribute("newParam", true);
    }
}
 