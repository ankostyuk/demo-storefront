package ru.nullpointer.storefront.web.support;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.WebRequest;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.support.ParamModel;
import ru.nullpointer.storefront.web.ui.DecimalEditor;
import ru.nullpointer.storefront.web.ui.ParamInput;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class ParamInputHelper {

    private static Logger logger = LoggerFactory.getLogger(ParamInputHelper.class);
    //
    private static final String OBJECT_NAME = "paramInput";

    public static Map<Integer, Integer> buildParamCountGroupMap(ParamModel paramModel, ParamInput paramInput) {
        Map<Integer, Integer> paramCountGroupMap = new HashMap<Integer, Integer>();

        for (Param p : paramModel.getParamList()) {
            if (paramInput.getP().get(p.getId()) != null) {
                Integer groupId = p.getParamGroupId();
                paramCountGroupMap.put(groupId, paramCountGroupMap.containsKey(groupId) ? paramCountGroupMap.get(groupId) + 1 : 1);
            }
        }

        return paramCountGroupMap;
    }

    public static InitBinderResult initBinder(ParamInput paramInput, ModelMap model, ParamModel paramModel) {
        return initBinder(paramInput, model, paramModel, true);
    }

    public static InitBinderResult initBinder(ParamInput paramInput, ModelMap model, ParamModel paramModel, boolean allowEmpty) {

        PropertyEditor booleanEditor = new CustomBooleanEditor(true);
        PropertyEditor numberEditor = new DecimalEditor(BigDecimal.class, true, 4);
        PropertyEditor selectEditor = new CustomNumberEditor(Integer.class, true);

        List<String> allowedFields = new ArrayList<String>();
        WebRequestDataBinder binder = new WebRequestDataBinder(paramInput, OBJECT_NAME);

        for (Param p : paramModel.getParamList()) {
            String paramPath = ParamInput.buildParamPath(p.getId());
            allowedFields.add(paramPath);
            switch (p.getType()) {
                case BOOLEAN:
                    binder.registerCustomEditor(Boolean.class, paramPath, booleanEditor);
                    break;
                case NUMBER:
                    binder.registerCustomEditor(BigDecimal.class, paramPath, numberEditor);
                    break;
                case SELECT:
                    binder.registerCustomEditor(Integer.class, paramPath, selectEditor);
                    break;
                default:
                    throw new RuntimeException("Неподдерживаемый тип параметра " + p.getType());
            }
        }
        binder.setAllowedFields(allowedFields.toArray(new String[0]));
        binder.setValidator(new ParamInputValidator(OBJECT_NAME, paramModel, allowEmpty));

        model.mergeAttributes(binder.getBindingResult().getModel());

        return new InitBinderResult(binder);
    }

    public static class InitBinderResult {

        private WebRequestDataBinder binder;

        public InitBinderResult(WebRequestDataBinder binder) {
            this.binder = binder;
        }

        public void bindAndValidate(WebRequest request) {
            binder.bind(request);
            binder.validate();

            ParamInput paramInput = (ParamInput) binder.getTarget();
            BindingResult result = binder.getBindingResult();

            paramInput.setErrors(result.hasErrors());

            logger.debug("bind result: {}", result);
            logger.debug("paramInput: {}", paramInput);
        }
    }
}
