package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.genre.GenreExistMapper;
import ru.yandex.practicum.filmorate.mappers.genre.GenresExistByFilmMapper;
import ru.yandex.practicum.filmorate.mappers.genre.GenresMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenresMapper mapper;
    private final GenreExistMapper existMapper;
    private final GenresExistByFilmMapper genresExistByFilmMapper;
    private static final String SQL_GET_ALL_GENRES = "SELECT * FROM genre;";
    private static final String SQL_GET_GENRES_BY_FILM_ID = "SELECT g.GENRE_ID, g.NAME FROM GENRE g RIGHT JOIN FILM_GENRE fg ON g.GENRE_ID = fg.GENRE_ID WHERE fg.FILM_ID = ?;";
    private static final String SQL_GET_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ?;";
    private static final String SQL_DELETE_GENRE_BY_ID = "DELETE FROM genre WHERE genre_id = ?;";
    private static final String SQL_CHECK_GENRE_EXISTS_BY_NAME = "SELECT COUNT(*) AS genre_exist FROM genre WHERE name = ?;";
    private static final String SQL_INSERT_FILM_GENRES = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private static final String SQL_DELETE_FILM_GENRES_BY_FILM_ID = "DELETE FROM film_genre WHERE film_id = ?;";
    private static final String SQL_CHECK_GENRE_EXISTS_BY_ID = "SELECT COUNT(*) AS genre_exist FROM genre WHERE genre_id = ?;";
    private static final String SQL_GENRE_EXISTS_BY_FILM_ID = "SELECT g.genre_id\n" +
            "FROM GENRE g \n" +
            "JOIN FILM_GENRE fg ON g.GENRE_ID = fg.GENRE_ID \n" +
            "WHERE fg.FILM_ID = ?";

    public Set<Genre> getGenresByFilm(Long filmId) {
        List<List<Genre>> query = jdbc.query(SQL_GET_GENRES_BY_FILM_ID, mapper, filmId);
        if (query.isEmpty()) {
            return new HashSet<>();
        }
        return new LinkedHashSet<>(query.get(0));
    }

    public Genre getGenreById(Long genreId) {
        List<List<Genre>> query = jdbc.query(SQL_GET_GENRE_BY_ID, mapper, genreId);
        if (query.isEmpty()) {
            return null;
        }
        return query.getFirst().getFirst();
    }

    public Collection<Genre> findAll() {
        return jdbc.query(SQL_GET_ALL_GENRES, mapper).getFirst();
    }

    public Long create(Genre genre) {
        Map<String, Object> genreMap = new HashMap<>();
        genreMap.put("name", genre.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("genre")
                .usingGeneratedKeyColumns("genre_id");

        return simpleJdbcInsert.executeAndReturnKey(genreMap).longValue();
    }

    public boolean delete(Long id) {
        return jdbc.update(SQL_DELETE_GENRE_BY_ID, id) > 0;
    }

    public boolean isGenreExistByName(String name) {
        return jdbc.query(SQL_CHECK_GENRE_EXISTS_BY_NAME, existMapper, name).getFirst();
    }

    public void insertFilmGenres(Long filmId, Set<Genre> genres) {
        genres.forEach(genre -> jdbc.update(SQL_INSERT_FILM_GENRES, filmId, genre.getId()));
    }

    public void updateGenres(Film updFilm) {
        Long id = updFilm.getId();
        deleteFilmGenres(id);

        List<Object[]> batchArgs = updFilm.getGenres().stream()
                .map(genre -> new Object[]{id, genre.getId()})
                .toList();

        jdbc.batchUpdate(SQL_INSERT_FILM_GENRES, batchArgs);
    }

    public void deleteFilmGenres(Long filmId) {
        jdbc.update(SQL_DELETE_FILM_GENRES_BY_FILM_ID, filmId);
    }

    public boolean isGenreExistId(Long genreId) {
        return jdbc.query(SQL_CHECK_GENRE_EXISTS_BY_ID, existMapper, genreId).getFirst();
    }

    public List<Long> isGenresExistByFilm(Long filmId) {
        return jdbc.query(SQL_GENRE_EXISTS_BY_FILM_ID, genresExistByFilmMapper, filmId).getFirst();
    }

}
