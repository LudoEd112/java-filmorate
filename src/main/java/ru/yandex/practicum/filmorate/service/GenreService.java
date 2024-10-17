package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;


    public Set<Genre> getGenre(Long filmId) {
        return genreDbStorage.getGenresByFilm(filmId);
    }

    public Genre getGenreById(Long genreId) {
        if (!genreDbStorage.isGenreExistId(genreId)) {
            throw new NotFoundException("Жанра с id %s не существует".formatted(genreId));
        }
        return genreDbStorage.getGenreById(genreId);
    }

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll().stream().toList();
    }

    public Long create(Genre genre) throws DuplicateEntityException {
        if (genreDbStorage.isGenreExistByName(genre.getName())) {
            throw new DuplicateEntityException("Жанр %s уже существует".formatted(genre.getName()));
        }
        return genreDbStorage.create(genre);
    }

    public boolean delete(Long genreId) {
        return genreDbStorage.delete(genreId);
    }

    public void insertGenresToFilm(Long id, Set<Genre> genres) {
        genreDbStorage.insertFilmGenres(id, genres);
    }

    public void deleteGenres(Long filmId) {
        genreDbStorage.deleteFilmGenres(filmId);
    }

    public void updateGenres(Film film) {
        genreDbStorage.updateGenres(film);
    }

    public List<Long> isGenresExist(Long filmId) {
        return genreDbStorage.isGenresExistByFilm(filmId);
    }


}
