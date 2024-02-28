package eu.domibus.connector.lib.spring.configuration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE, FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = StoreLoadableValidator.class)
@Documented
@NotNull
public @interface CheckStoreIsLoadable {

    String message() default "{eu.domibus.connector.lib.spring.configuration.validation.cannot_load_key_store}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
