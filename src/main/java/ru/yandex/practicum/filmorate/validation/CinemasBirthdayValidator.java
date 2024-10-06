package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CinemasBirthdayValidator implements ConstraintValidator<CinemasBirthday, LocalDate> {
    private static final LocalDate CINEMAS_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(CINEMAS_BIRTHDAY);
    }
}
