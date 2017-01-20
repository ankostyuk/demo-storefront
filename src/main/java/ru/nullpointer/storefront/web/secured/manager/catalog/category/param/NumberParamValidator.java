package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nullpointer.storefront.domain.param.NumberParam;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class NumberParamValidator implements Validator {

    @Autowired
    private BeanValidator validator;

    @Override
    public boolean supports(Class<?> type) {
        return NumberParam.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        NumberParam param = (NumberParam) o;
        validator.validate(param, errors);

        BigDecimal min = param.getMinValue();
        BigDecimal max = param.getMaxValue();
        if (min != null && max != null && max.compareTo(min) <= 0) {
            errors.reject("validation." + errors.getObjectName() + ".invalidMinMax",
                    "Максимальное значение должно быть больше минимального");
        }
    }
}
