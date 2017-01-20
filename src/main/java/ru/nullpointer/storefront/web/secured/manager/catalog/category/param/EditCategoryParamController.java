package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.util.StringIndexComparator;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Controller
public class EditCategoryParamController {

    private Logger logger = LoggerFactory.getLogger(EditCategoryParamController.class);
    //
    @Resource
    private ParamService paramService;
    @Autowired
    private CategoryParamHelper categoryParamHelper;

    private Param getParam(Integer paramId) {
        Param param = paramService.getParamById(paramId);
        if (param == null) {
            throw new NotFoundException("Параметр категории с id " + paramId + " не найден");
        }
        return param;
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/edit/{id}", method = RequestMethod.GET)
    public String handleGet(
            @PathVariable("id") Integer paramId,
            ModelMap model) {

        Param param = getParam(paramId);
        switch (param.getType()) {
            case BOOLEAN:
                return handleGetBoolean(param, model);

            case NUMBER:
                return handleGetNumber((NumberParam) param, model);

            case SELECT:
                return handleGetSelect(param, model);

            default:
                throw new NotFoundException();
        }
    }

    @RequestMapping(value = "/secured/manager/catalog/category/param/edit/{id}", method = RequestMethod.POST)
    public String handlePost(
            @PathVariable("id") Integer paramId,
            @RequestParam(value = "continue", required = false) String cont,
            ModelMap model, WebRequest request) {

        Param param = getParam(paramId);
        switch (param.getType()) {
            case BOOLEAN:
                return handlePostBoolean(param, model, request);

            case NUMBER:
                return handlePostNumber((NumberParam) param, model, request);

            case SELECT:
                return handlePostSelect(param, model, request, cont != null);

            default:
                throw new NotFoundException();
        }
    }

    private String handleGetBoolean(Param param, ModelMap model) {
        initModel(param, model);
        categoryParamHelper.initBooleanBinder(param, model);
        return "secured/manager/catalog/category/param/boolean/edit";
    }

    private String handlePostBoolean(Param param, ModelMap model, WebRequest request) {
        initModel(param, model);
        WebRequestDataBinder binder = categoryParamHelper.initBooleanBinder(param, model);

        binder.bind(request);
        binder.validate();

        if (binder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/boolean/edit";
        }

        paramService.updateParamInfo(param);

        return categoryParamHelper.buildRedirectUrl(model);
    }

    private String handleGetNumber(NumberParam param, ModelMap model) {
        initModel(param, model);
        categoryParamHelper.initNumberBinder(param, model);
        categoryParamHelper.initNumberParamUnit(param, model);

        return "secured/manager/catalog/category/param/number/edit";
    }

    private String handlePostNumber(NumberParam param, ModelMap model, WebRequest request) {
        initModel(param, model);
        WebRequestDataBinder binder = categoryParamHelper.initNumberBinder(param, model);
        binder.bind(request);

        categoryParamHelper.initNumberParamUnit(param, model);

        binder.validate();
        if (binder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/number/edit";
        }

        paramService.updateParamInfo(param);

        return categoryParamHelper.buildRedirectUrl(model);
    }

    private String handleGetSelect(Param param, ModelMap model) {
        initModel(param, model);
        categoryParamHelper.initSelectBinder(param, model);
        categoryParamHelper.initOptionsBinder(initOptions(param), model);

        return "secured/manager/catalog/category/param/select/edit";
    }

    private String handlePostSelect(Param param, ModelMap model, WebRequest request, boolean cont) {
        initModel(param, model);

        WebRequestDataBinder binder = categoryParamHelper.initSelectBinder(param, model);
        binder.bind(request);
        binder.validate();

        WebRequestDataBinder optionsBinder = categoryParamHelper.initOptionsBinder(initOptions(param), model);
        optionsBinder.bind(request);
        optionsBinder.validate();

        if (binder.getBindingResult().hasErrors() || optionsBinder.getBindingResult().hasErrors()) {
            return "secured/manager/catalog/category/param/select/edit";
        }

        paramService.updateParamInfo(param);
        paramService.updateParamSelectOptions(param.getId(), getModifiedOptions(model));
        paramService.addParamSelectOptions(param.getId(), getNewOptions(model));

        if (cont) {
            // вернуться к редактированию
            return "redirect:/secured/manager/catalog/category/param/edit/" + param.getId();
        }
        return categoryParamHelper.buildRedirectUrl(model);
    }

    @SuppressWarnings("unchecked")
    private List<ParamSelectOption> getNewOptions(ModelMap model) {
        SortedMap<String, Object> sortedMap = new TreeMap<String, Object>(new StringIndexComparator());
        sortedMap.putAll(((OptionsValueMap) model.get("selectOptions")).getValue());

        List<ParamSelectOption> result = new ArrayList<ParamSelectOption>();
        for (String key : sortedMap.keySet()) {
            ParamSelectOption option = (ParamSelectOption) sortedMap.get(key);
            String name = option.getName();
            if (option.getId() == null && name != null && !name.trim().isEmpty()) {
                result.add(option);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<ParamSelectOption> getModifiedOptions(ModelMap model) {
        SortedMap<String, Object> sortedMap = new TreeMap<String, Object>(new StringIndexComparator());
        sortedMap.putAll(((OptionsValueMap) model.get("selectOptions")).getValue());

        List<ParamSelectOption> result = new ArrayList<ParamSelectOption>();
        for (String key : sortedMap.keySet()) {
            ParamSelectOption option = (ParamSelectOption) sortedMap.get(key);
            if (option.getId() != null) {
                result.add(option);
            }
        }
        return result;
    }

    private OptionsValueMap initOptions(Param param) {
        List<ParamSelectOption> optionList = paramService.getParamSelectOptions(param);
        OptionsValueMap options = new OptionsValueMap();
        int i = 0;
        for (ParamSelectOption option : optionList) {
            options.getValue().put(String.valueOf(i++), option);
        }
        options.getValue().put(String.valueOf(i), new ParamSelectOption());
        return options;
    }

    private void initModel(Param param, ModelMap model) {
        ParamGroup paramGroup = paramService.getParamGroupById(param.getParamGroupId());
        CatalogItem categoryItem = paramService.getCategoryItemByParamId(param.getId());

        model.addAttribute("paramGroup", paramGroup);
        model.addAttribute("catalogItem", categoryItem);
        model.addAttribute("newParam", false);
        model.addAttribute("paramId", param.getId());
    }
}
