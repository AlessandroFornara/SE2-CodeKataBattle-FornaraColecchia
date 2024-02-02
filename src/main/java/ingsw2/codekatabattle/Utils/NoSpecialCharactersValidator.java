package ingsw2.codekatabattle.Utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Custom validator for the NoSpecialCharacters constraint.
 * This validator implements the ConstraintValidator interface, providing the logic to validate that a string
 * does not contain special characters.
 */
public class NoSpecialCharactersValidator implements ConstraintValidator<NoSpecialCharacters, String> {

    private static final String SPECIAL_CHARACTERS_REGEX = "[^a-zA-Z0-9]+";

    @Override
    public void initialize(NoSpecialCharacters constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !Pattern.compile(SPECIAL_CHARACTERS_REGEX).matcher(value).find();
    }
}
