package ru.nullpointer.storefront.web.secured.manager.catalog.category.param;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;
import ru.nullpointer.storefront.validation.BeanValidator;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class OptionsValueMapValidator implements Validator {

    @Autowired
    private BeanValidator validator;

    @Override
    public boolean supports(Class<?> type) {
        return OptionsValueMap.class.equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        OptionsValueMap valueMap = (OptionsValueMap) o;

        int validatedCount = 0;
        for (String key : valueMap.getValue().keySet()) {
            ParamSelectOption option = (ParamSelectOption) valueMap.getValue().get(key);
            Integer id = option.getId();
            String name = option.getName();
            // валидировать только если старый вариант выбора (id != null)
            // или новый вариант (id == null) с непустым именем
            if ((id != null) || (name != null && !name.trim().isEmpty())) {
                errors.pushNestedPath(OptionsValueMap.buildObjectPath(key));
                validator.validate(option, errors);
                errors.popNestedPath();

                validatedCount++;
            }
        }

        if (validatedCount == 0) {
            // ни одного поля не валидировали
            errors.reject("validation.selectOptions.required");
        }
    }
}
