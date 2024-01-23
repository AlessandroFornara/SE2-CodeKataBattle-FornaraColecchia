package ingsw2.codekatabattle.Utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpecialCharactersValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpecialCharacters {
    String message() default "String must not contain special characters or spaces";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
