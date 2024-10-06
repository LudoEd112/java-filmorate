package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankLoginValidator implements ConstraintValidator<NotBlankLogin, String> {

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return (containsWhiteSpace(login));
    }

    public static boolean containsWhiteSpace(final String login) {
        if (login != null) {
            for (int i = 0; i < login.length(); i++) {
                if (Character.isWhitespace(login.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
