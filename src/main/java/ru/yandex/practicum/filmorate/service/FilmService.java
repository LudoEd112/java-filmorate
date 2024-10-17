package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectDataException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

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
        Film newFilm = filmStorage.createFilm(film);
        mpaService.addMpa(film.getMpa(), newFilm.getId());
        boolean exist = genreService.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet())
                .containsAll(film.getGenres().stream()
                        .map(Genre::getId)
                        .toList());
        if (!exist) {
            throw new IncorrectDataException("Не существует одного из представленных жанров - %s"
                    .formatted(film.getGenres()));
        }
        newFilm.setGenres(film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))  // Сортировка по id жанра
                .collect(Collectors.toCollection(LinkedHashSet::new)));
        genreService.insertGenresToFilm(newFilm.getId(), film.getGenres());
        return newFilm;
    }


    public Film update(Film film) {
        log.debug("Обновляем фильм: {}", film);
        Film oldFilm = filmStorage.getFilmById(film.getId());
        if (film.getGenres() != null) {
            genreService.deleteGenres(film.getId());
            genreService.updateGenres(film);
        }
        if (film.getName() != null) {
            oldFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getMpa() != null) {
            mpaService.addMpa(film.getMpa(), film.getId());
        }
        filmStorage.updateFilm(oldFilm);
        return oldFilm;


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