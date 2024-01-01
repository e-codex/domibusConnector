package eu.ecodex.utils.configuration.example1.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static eu.ecodex.utils.configuration.example1.validators.StringComparisonMode.EQUAL;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy=CompareStringsValidator.class)
@Documented
public @interface CompareStrings {
    String[] propertyNames();
    StringComparisonMode matchMode() default EQUAL;
    boolean allowNull() default false;
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
