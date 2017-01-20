package ru.nullpointer.storefront.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Alexander Yastrebov
 */
public class UrlValidator implements ConstraintValidator<Url, String> {

    private org.apache.commons.validator.UrlValidator validator;

    @Override
    public void initialize(Url constraintAnnotation) {
        validator = new org.apache.commons.validator.UrlValidator();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        return validator.isValid(value);
    }
}
