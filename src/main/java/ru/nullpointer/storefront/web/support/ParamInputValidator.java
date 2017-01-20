package ru.nullpointer.storefront.web.support;

import ru.nullpointer.storefront.web.ui.ParamInput;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.domain.support.ParamModel;

/**
 *
 * @author Alexander Yastrebov
 */
public class ParamInputValidator implements Validator {

    private String objectErrorCodePrefix;
    private String fieldErrorCodePrefix;
    private ParamModel paramModel;
    private boolean allowEmpty;

    public ParamInputValidator(String objectName, ParamModel paramModel, boolean allowEmpty) {
        this.paramModel = paramModel;
        this.allowEmpty = allowEmpty;

        objectErrorCodePrefix = "validation." + objectName + ".";
        fieldErrorCodePrefix = objectErrorCodePrefix + "p.";
    }

    @Override
    public boolean supports(Class<?> type) {
        return ParamInput.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        boolean empty = true;

        ParamInput paramInput = (ParamInput) o;
        for (Param p : paramModel.getParamList()) {
            Object paramValue = paramInput.getParamValueMap().get(p.getId());
            if (paramValue != null) {
                empty = false;
                switch (p.getType()) {
                    case BOOLEAN:
                        // нечего делать
                        break;
                    case NUMBER: {
                        BigDecimal value = (BigDecimal) paramValue;

                        NumberParam numberParam = (NumberParam) p;
                        BigDecimal minValue = numberParam.getMinValue();
                        BigDecimal maxValue = numberParam.getMaxValue();

                        int minCmp = value.compareTo(minValue);
                        int maxCmp = value.compareTo(maxValue);
                        if (minCmp < 0 || maxCmp > 0) {
                            errors.rejectValue(ParamInput.buildParamPath(p.getId()),
                                    fieldErrorCodePrefix + (minCmp < 0 ? "size.min" : "size.max"),
                                    new Object[]{maxValue, minValue}, "");
                        }
                    }
                    break;
                    case SELECT: {
                        Integer value = (Integer) paramValue;

                        boolean found = false;
                        List<ParamSelectOption> optionList = paramModel.getSelectOptionMap().get(p.getId());
                        for (ParamSelectOption option : optionList) {
                            if (value.equals(option.getId())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            errors.rejectValue(ParamInput.buildParamPath(p.getId()), fieldErrorCodePrefix + "nooption");
                        }
                    }
                    break;
                    default:
                        throw new RuntimeException("Неподдерживаемый тип параметра " + p.getType());
                }
            }
        }

        if (!allowEmpty && empty) {
            errors.reject(objectErrorCodePrefix + "empty");
        }
    }
}
