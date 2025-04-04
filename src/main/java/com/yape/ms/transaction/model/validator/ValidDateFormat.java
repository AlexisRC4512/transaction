package com.yape.ms.transaction.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFormat {
    String message() default "La fecha debe estar en el formato ddMMyyyy";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
