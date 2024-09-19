package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CinemasBirthdayValidator.class)
@Documented
public @interface CinemasBirthday {
    String message() default "Дата релиза должна быть позже 1895-12-25";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
