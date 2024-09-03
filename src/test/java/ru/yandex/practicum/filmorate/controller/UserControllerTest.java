package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void init(){
        user = User.builder()
                .id(1L)
                .email("datakmakov@yandex.ru")
                .login("LudoEd112")
                .name("Dmitrii")
                .birthday(LocalDate.of(2004, 10, 22))
                .build();
    }

    @Test
    void shouldUserCreate() {

        assertNotNull(user, "Пользователь не создан");
        assertEquals(user.getId(), user.getId(), "Неверный ид созданного пользователя");
        assertEquals(user.getName(), user.getName(), "Неверное имя созданного пользователя");
        assertEquals(user.getEmail(), user.getEmail(), "Неверный email созданного пользователя");
        assertEquals(user.getLogin(), user.getLogin(), "Неверный логин созданного пользователя");
        assertEquals(user.getBirthday(), user.getBirthday(), "Неверная дата рождения созданного пользователя");
    }

    @Test
    void shouldNotValidUserWithEmptyLogin() {
        user.setLogin(null);

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            var violations = validator.validate(user);
            assertEquals(1, violations.stream()
                    .toList().size(), "Валидация не выполнена");
            assertEquals("NotBlank",
                    violations.stream()
                            .toList()
                            .getFirst()
                            .getConstraintDescriptor()
                            .getAnnotation().annotationType().getSimpleName(),
                    "Не работает валидация на пустой логин пользователя");
        }
    }

    @Test
    void shouldNotValidUsersFormatEmail() {
        user.setEmail("datakmakov/yandex.ru");

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            var violations = validator.validate(user);
            assertEquals(1, violations.stream()
                    .toList().size(), "Валидация не выполнена");
            assertEquals("Email",
                    violations.stream()
                            .toList()
                            .getFirst()
                            .getConstraintDescriptor()
                            .getAnnotation().annotationType().getSimpleName(),
                    "Не работает валидация на некорректную почту пользователя");
        }
    }

    @Test
    void shouldNotValidUserWithNullEmail() {
        user.setEmail(null);

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            var violations = validator.validate(user);
            assertEquals(1, violations.stream()
                    .toList().size(), "Валидация не выполнена");
            assertEquals("NotNull",
                    violations.stream()
                            .toList()
                            .getFirst()
                            .getConstraintDescriptor()
                            .getAnnotation().annotationType().getSimpleName(),
                    "Не работает валидация на пустую почту пользователя");
        }
    }

    @Test
    void shouldNotValidUserWithBirthdayInFuture() {
        user.setBirthday(LocalDate.of(2026, 10, 22));

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            var violations = validator.validate(user);
            assertEquals(1, violations.stream()
                    .toList().size(), "Валидация не выполнена");
            assertEquals("PastOrPresent",
                    violations.stream()
                            .toList()
                            .getFirst()
                            .getConstraintDescriptor()
                            .getAnnotation().annotationType().getSimpleName(),
                    "Не работает валидация на некорректную дату рождения пользователя");
        }
    }
}