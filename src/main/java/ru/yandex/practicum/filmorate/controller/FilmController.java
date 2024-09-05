package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    private Long filmsId = 0L;

    private final LocalDate cinemasBirthday = LocalDate.of(1895, 12, 25);

    private final int descriptionLength = 200;

    private final int minDuration = 0;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film == null) {
            throw new ValidateException("Film is null");
        }
        checkValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавили фильм");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new NotExistException("Поле id нет в запросе");
        }
        checkValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Такой фильм еще не добавлен");
        }
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setReleaseDate(film.getReleaseDate());
        log.info("Обновили фильм");
        return oldFilm;
    }

    public void checkValidation(Film film) {
        if (film.getName() == null || film.getName().trim().isBlank()) {
            throw new ValidateException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > descriptionLength) {
            throw new ValidateException("Максимальная длина описания фильма");
        }
        if (film.getReleaseDate().equals(cinemasBirthday) ||
                film.getReleaseDate().isBefore(cinemasBirthday)) {
            throw new ValidateException("Дата релиза должна быть позже 1895-12-25");
        }
        if (film.getDuration() < minDuration) {
            throw new ValidateException("Продолжительность фильма не может быть отрицательной");
        }
    }

    private long getNextId() {
        return ++filmsId;
    }
}
