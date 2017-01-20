package ru.nullpointer.storefront.validation;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class BeanValidator implements org.springframework.validation.Validator, InitializingBean {

    public static final String VALIDATION_PREFIX = "validation";
    private static Logger logger = LoggerFactory.getLogger(BeanValidator.class);
    //
    private Validator validator;

    @Override
    public void validate(Object obj, Errors errors) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        for (ConstraintViolation<Object> violation : violations) {
            Object[] errorArgs = buildErrorArgs(violation);

            // TODO
            String groupName = "";

            String className = violation.getRootBeanClass().getSimpleName();
            String propertyPath = violation.getPropertyPath().toString();
            String constraint = getConstraint(violation.getMessageTemplate());

            StringBuilder sb = new StringBuilder();
            sb.append(VALIDATION_PREFIX);
            sb.append(".");

            if (!groupName.isEmpty()) {
                sb.append(groupName).append(".");

            }
            sb.append(className);
            sb.append(".");
            sb.append(violation.getPropertyPath());
            sb.append(".");
            sb.append(constraint);

            String errorCode = sb.toString();

            String defaultMessage = errorCode;

            errors.rejectValue(propertyPath, errorCode, errorArgs, defaultMessage);

            logger.debug("Property path: {}, error code: {}, errorArgs: {}, defaultMessage: {}", new Object[]{
                        propertyPath,
                        errorCode,
                        errorArgs,
                        defaultMessage
                    });
        }
    }

    private String getConstraint(String messageTemplate) {
        String result = messageTemplate.trim();
        final String prefix = "{constraint.";
        final String suffix = "}";
        // отрезать префикс и суффикс
        if (result.startsWith(prefix)) {
            result = result.substring(prefix.length());
            if (result.endsWith(suffix)) {
                result = result.substring(0, result.length() - suffix.length());
            }
        }
        return result;
    }

    private Object[] buildErrorArgs(ConstraintViolation<Object> violation) {
        Map<String, Object> attrMap = violation.getConstraintDescriptor().getAttributes();
        // сортировка по алфавиту
        Set<String> paramNameSet = new TreeSet<String>(attrMap.keySet());
        // удалить лишние
        paramNameSet.remove("message");
        paramNameSet.remove("groups");
        paramNameSet.remove("payload");

        Object[] result = new Object[paramNameSet.size()];
        int i = 0;
        for (String paramName : paramNameSet) {
            result[i++] = attrMap.get(paramName);
        }
        return result;
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }
}
