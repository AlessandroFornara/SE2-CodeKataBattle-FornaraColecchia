package ingsw2.codekatabattle.Utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

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
