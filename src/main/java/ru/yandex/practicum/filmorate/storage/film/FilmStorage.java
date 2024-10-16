package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    Film createFilm(Film film);

    Film updateFilm(Film film) throws InternalServerException;

    void addLike(Film film, User user) throws DuplicateEntityException, InternalServerException;

    void removeLike(Film film, User user) throws InternalServerException;

    Collection<Film> getPopularFilms(int count);
}
