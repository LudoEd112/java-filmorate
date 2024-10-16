package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;

    public Film create(Film film) {
        log.debug("Создаем фильм: {}", film);
        return filmStorage.createFilm(film);
    }

    public Film update(Film film) throws InternalServerException, IncorrectDataException {
        log.debug("Обновляем фильм: {}", film);
        Film newFilm = filmStorage.updateFilm(film);
        mpaService.addMpa(film.getMpa(), newFilm.getId());
        boolean exist = new HashSet<>(
                genreService.findAll().stream()
                        .map(Genre::getId)
                        .toList()
        )
                .containsAll(
                        film.getGenres().stream()
                                .map(Genre::getId)
                                .toList()
                );
        if (!exist) {
            throw new IncorrectDataException("Не существует одного из представленных жанров - %s"
                    .formatted(film.getGenres()));
        }
        genreService.insertGenresToFilm(newFilm.getId(), film.getGenres());
        return getFilmById(newFilm.getId());
    }

    public void addLike(long filmId, long userId) throws DuplicateEntityException, InternalServerException {
        if (userService.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователя с id " + userId + " не существует");
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new EntityNotFoundException("Фильма с id " + filmId + " не существует");
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        log.debug("Добавляем лайк фильму {}, от пользователя {}", film, user);
        filmStorage.addLike(film, user);
    }

    public void removeLike(long filmId, long userId) throws InternalServerException {
        if (userService.getUserById(userId) == null) {
            throw new EntityNotFoundException("Пользователя с id " + userId + " не существует");
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new EntityNotFoundException("Фильма с id " + filmId + " не существует");
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        log.debug("Пользователь {} убирает лайк с фильма {}", user, film);
        filmStorage.removeLike(film, user);
        log.debug("Лайк от пользователя {} убран с фильма {}", user, film);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Получаем все фильмы");
        return filmStorage.getAllFilms().stream().toList();
    }

    public Film getFilmById(long id) {
        log.debug("Get film by id: {}", id);
        Film film = filmStorage.getFilmById(id);
        film.setMpa(mpaService.getMpaByFilm(id));
        film.setLikes(filmStorage.getLikes(id));
        film.setGenres(genreService.getGenre(id));
        return film;
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получаем популярные фильмы");
        return filmStorage.getPopularFilms(count).stream().toList();
    }
}