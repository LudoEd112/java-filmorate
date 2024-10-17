/*
package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmorateApplicationTests {
	private Film film;

	@BeforeEach
	void init() {
		film = Film.builder()
				.id(1L)
				.name("Титаник")
				.description("Романтический фильм про корблекрушение")
				.releaseDate(LocalDate.of(1997, 12, 16))
				.duration(194L)
				.build();
	}

	@Test
	void shouldCreateFilm() {

		assertNotNull(film, "Экзмепляр фильма не создан");
		assertEquals(film.getName(), film.getName(), "Неверное наименование созданного фильма");
		assertEquals(film.getDescription(), film.getDescription(), "Неверное описание созданного фильма");
		assertEquals(film.getReleaseDate(), film.getReleaseDate(), "Неверная дата выпуска созданного фильма");
		assertEquals(film.getDuration(), film.getDuration(), "Неверная длительность созданного фильма");
	}

	@Test
	void shouldNotValidFilmWithEmptyName() {
		film.setName(null);

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();
			var violations = validator.validate(film);
			assertEquals(1, violations.stream()
					.toList().size(), "Валидация не выполнена");
			assertEquals("NotBlank",
					violations.stream()
							.toList()
							.getFirst()
							.getConstraintDescriptor()
							.getAnnotation().annotationType().getSimpleName(),
					"Не работает валидация на пустое наименование фильма");
		}
	}

	@Test
	void shouldNotValidFilmWithLargeDescription() {
		film.setDescription("Романтический фильм про корблекрушение".repeat(201));

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();
			var violations = validator.validate(film);
			assertEquals(1, violations.stream()
					.toList().size(), "Валидация не выполнена");
			assertEquals("Size",
					violations.stream()
							.toList()
							.getFirst()
							.getConstraintDescriptor()
							.getAnnotation().annotationType().getSimpleName(),
					"Не работает валидация на длинное описание фильма");
		}
	}

	@Test
	void shouldNotValidFilmWithNegativeDuration() {
		film.setDuration(-5L);

		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			Validator validator = validatorFactory.getValidator();
			var violations = validator.validate(film);
			assertEquals(1, violations.stream()
					.toList().size(), "Валидация не выполнена");
			assertEquals("Positive",
					violations.stream()
							.toList()
							.getFirst()
							.getConstraintDescriptor()
							.getAnnotation().annotationType().getSimpleName(),
					"Не работает валидация положительное значение длительности фильма");
		}
	}
}*/
