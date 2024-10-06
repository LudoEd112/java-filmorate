package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Film create(Film film) {
        log.debug("Создаем фильм: {}", film);
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) {
        log.debug("Обновляем фильм: {}", film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        log.debug("Добавляем лайк фильму {}, от пользователя {}", film, user);
        filmStorage.addLike(film, user);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        log.debug("Пользователь {} убирает лайк с фильма {}", user, film);
        filmStorage.removeLike(film, user);
        log.debug("Лайк от пользователя {} убран с фильма {}", user, film);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Получаем все фильмы");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        log.debug("Get film by id: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with id: %s not found".formatted(id)));
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получаем популярные фильмы");
        return filmStorage.getPopularFilms(count);
    }
}