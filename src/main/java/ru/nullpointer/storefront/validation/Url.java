package ru.nullpointer.storefront.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author Alexander Yastrebov
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = UrlValidator.class)
@Documented
public @interface Url {

    String message() default "{ru.nullpointer.constraints.url}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
