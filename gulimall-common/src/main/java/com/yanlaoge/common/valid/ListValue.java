package com.yanlaoge.common.valid;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * ListValue
 *
 * @author rubyle
 */
@Documented
@Constraint(validatedBy = {ListValueConstraintValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {

	String message() default "{com.yanlaoge.common.valid.ListValue.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	int[] vals() default {};
}
