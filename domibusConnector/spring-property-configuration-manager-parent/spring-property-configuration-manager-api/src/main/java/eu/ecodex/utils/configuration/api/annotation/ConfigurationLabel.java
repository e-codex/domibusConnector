package eu.ecodex.utils.configuration.api.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Inherited
@Documented
public @interface ConfigurationLabel {

    @AliasFor("label")
    public String value() default "";

    @AliasFor("value")
    public String label() default "";
}
