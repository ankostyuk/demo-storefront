package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebRequestDataBinder;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamGroup;
import ru.nullpointer.storefront.service.ParamService;
import ru.nullpointer.storefront.service.UnitService;
import ru.nullpointer.storefront.validation.BeanValidator;
import ru.nullpointer.storefront.web.ui.DecimalEditor;
import ru.nullpointer.storefront.web.ui.catalog.ParamGroupProperties;
import ru.nullpointer.storefront.web.ui.catalog.ParamProperties;

/**
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
@Component
public class CategoryParamHelper {

    @Resource
    private MessageSource messageSource;
    @Resource
    private ParamService paramService;
    @Resource
    private UnitService unitService;
    @Autowired
    private BeanValidator beanValidator;
    @Autowired
    private NumberParamValidator numberParamValidator;
    @Autowired
    private OptionsValueMapValidator optionsValidator;

    public ParamGroupProperties buildNewParamGroupProperties(Category category) {
        ParamGroup paramGroup = new ParamGroup();
        Integer psdId = category.getParameterSetDescriptorId();
        if (psdId == null) {
            // параметры еще не сформированы - предложить наименование группы по-умолчанию
            paramGroup.setName(messageSource.getMessage("ui.catalog.category.param.group.first.name.default", null, null));
        }
        ParamGroupProperties paramGroupProperties = new ParamGroupProperties();
        paramGroupProperties.setParamGroup(paramGroup);
        paramGroupProperties.setCanDelete(false);
        return paramGroupProperties;
    }

    public ParamGroupProperties buildParamGroupProperties(ParamGroup paramGroup, boolean baseParams) {
        Assert.notNull(paramGroup);

        ParamGroupProperties paramGroupProperties = new ParamGroupProperties();
        paramGroupProperties.setParamGroup(paramGroup);

        List<ParamProperties> paramList = new ArrayList<ParamProperties>();
        List<Param> groupParams = paramService.getParamList(paramGroup.getId());
        for (Param param : groupParams) {
            if (baseParams) {
                if (param.getBase()) {
                    paramList.add(buildParamProperties(param));
                }
            } else {
                paramList.add(buildParamProperties(param));
            }
        }
        paramGroupProperties.setParamList(paramList);

        paramGroupProperties.setCanDelete(paramService.canDeleteParamGroup(paramGroup));

        return paramGroupProperties;
    }

    public ParamProperties buildParamProperties(Param param) {
        Assert.notNull(param);

        ParamProperties paramProperties = new ParamProperties();

        paramProperties.setParam(param);
        paramProperties.setCanDelete(paramService.canDeleteParam(param));

        return paramProperties;
    }

    WebRequestDataBinder initBooleanBinder(Param param, ModelMap model) {
        WebRequestDataBinder binder = initBinder(param, model, beanValidator,
                "trueName",
                "falseName");
        return binder;
    }

    WebRequestDataBinder initNumberBinder(NumberParam param, ModelMap model) {
        WebRequestDataBinder binder = initBinder(param, model, numberParamValidator,
                "minValue",
                "maxValue",
                "unitId",
                "precision");
        binder.registerCustomEditor(BigDecimal.class, new DecimalEditor(BigDecimal.class, true, 4));
        return binder;
    }

    WebRequestDataBinder initSelectBinder(Param param, ModelMap model) {
        WebRequestDataBinder binder = initBinder(param, model, beanValidator, new String[0]);
        return binder;
    }

    WebRequestDataBinder initOptionsBinder(OptionsValueMap options, ModelMap model) {
        WebRequestDataBinder binder = new WebRequestDataBinder(options, "selectOptions");
        binder.setAllowedFields("*name");
        binder.setValidator(optionsValidator);
        model.mergeAttributes(binder.getBindingResult().getModel());
        return binder;
    }

    void initNumberParamUnit(NumberParam param, ModelMap model) {
        Integer unitId = param.getUnitId();
        if (unitId != null) {
            Unit unit = unitService.getUnitById(unitId);
            if (unit == null) {
                param.setUnitId(null);
            } else {
                model.addAttribute("unit", unit);
            }
        }
    }

    String buildRedirectUrl(ModelMap model) {
        CatalogItem item = (CatalogItem) model.get("catalogItem");
        StringBuilder sb = new StringBuilder()//
                .append("redirect:/secured/manager/catalog/category/param/")//
                .append(item.getId());
        return sb.toString();
    }

    private WebRequestDataBinder initBinder(Param param, ModelMap model, Validator validator, String... extraAllowedFields) {
        WebRequestDataBinder binder = new WebRequestDataBinder(param, "param");

        List<String> allowedFields = new ArrayList<String>();
        allowedFields.add("name");
        allowedFields.add("description");
        allowedFields.add("base");
        for (String s : extraAllowedFields) {
            allowedFields.add(s);
        }

        binder.setAllowedFields(allowedFields.toArray(new String[0]));
        binder.setValidator(validator);
        model.mergeAttributes(binder.getBindingResult().getModel());
        return binder;
    }
}
