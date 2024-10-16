package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;/*
import ru.yandex.practicum.filmorate.exception.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;*/
import ru.yandex.practicum.filmorate.exceptions.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.mappers.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.film.LikeCheckMapper;
import ru.yandex.practicum.filmorate.mappers.film.LikeMapper;
import ru.yandex.practicum.filmorate.mappers.film.RatedFilmsMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final LikeMapper likeMapper;
    private final LikeCheckMapper likeCheckMapper;
    private final RatedFilmsMapper ratedMapper;
    private static final String ENTITY_NOT_FOUND = "Фильм с id %s не найден";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration_minutes = ? WHERE id = ?;";
    private static final String ADD_LIKE_QUERY = "INSERT INTO FILM_LIKE(FILM_ID, USER_ID) VALUES (?, ?);";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM FILM_LIKE WHERE film_id = ? AND user_id = ?;";
    private static final String LIKE_CHECK_QUERY = "SELECT COUNT(*) AS likes FROM film_like WHERE film_id = ? AND user_id = ?";
    private static final String GET_FILM_QUERY = "SELECT * FROM FILMS f WHERE id = ?;";
    private static final String GET_LIKES_QUERY = "SELECT user_id FROM film_like fl WHERE film_id = ?;";
    private static final String FIND_ALL_QUERY = "SELECT F.ID,\n" +
            "                   F.NAME,\n" +
            "                   F.DESCRIPTION,\n" +
            "                   F.RELEASE_DATE,\n" +
            "                   F.DURATION_MINUTES,\n" +
            "                   F.RATING_ID,\n" +
            "                   R.NAME AS r_name,\n" +
            "                   LISTAGG(DISTINCT CONCAT(G.GENRE_ID, '/', G.NAME)) FILTER (WHERE G.GENRE_ID IS NOT NULL) AS GENRES,\n" +
            "                   LISTAGG(DISTINCT FL.USER_ID) AS LIKES\n" +
            "            FROM FILMS F\n" +
            "            LEFT JOIN RATING R ON F.RATING_ID = R.RATING_ID\n" +
            "            LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
            "            LEFT JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID\n" +
            "            LEFT JOIN FILM_LIKE FL ON F.ID = FL.FILM_ID\n" +
            "            GROUP BY F.ID\n" +
            "            ORDER BY f.ID;";
    private static final String GET_RATED_FILMS_QUERY = "SELECT F.ID,\n" +
            "                   F.NAME,\n" +
            "                   F.DESCRIPTION,\n" +
            "                   F.RELEASE_DATE,\n" +
            "                   F.DURATION_MINUTES,\n" +
            "                   F.RATING_ID,\n" +
            "                   R.NAME AS r_name,\n" +
            "                   LISTAGG(DISTINCT CONCAT(G.GENRE_ID, '/', G.NAME)) FILTER (WHERE G.GENRE_ID IS NOT NULL) AS GENRES,\n" +
            "                   LISTAGG(DISTINCT FL.USER_ID) AS LIKES,\n" +
            "                   COUNT(DISTINCT fl.USER_ID) AS rate\n" +
            "            FROM FILMS F\n" +
            "            LEFT JOIN RATING R ON F.RATING_ID = R.RATING_ID\n" +
            "            LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
            "            LEFT JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID\n" +
            "            LEFT JOIN FILM_LIKE FL ON F.ID = FL.FILM_ID\n" +
            "            GROUP BY F.ID\n" +
            "            ORDER BY rate DESC, f.ID\n" +
            "            LIMIT ?;";

    @Override
    public Collection<Film> getAllFilms() {
        return Optional.of(jdbc.query(FIND_ALL_QUERY, ratedMapper).getFirst())
                .orElseThrow(() -> new EntityNotFoundException("Не удалось получить фильмы"));
    }

    @Override
    public Film getFilmById(long id) {
        return Optional.ofNullable(jdbc.queryForObject(GET_FILM_QUERY, mapper, id).getFirst())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND.formatted(id)));
    }

    @Override
    public Film createFilm(Film film) {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", film.getName());
        filmMap.put("description", film.getDescription());
        filmMap.put("release_date", java.sql.Date.valueOf(film.getReleaseDate()));
        filmMap.put("duration_minutes", film.getDuration());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(filmMap).longValue();
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) throws InternalServerException {
        int rowsUpdated = jdbc.update(UPDATE_FILM_QUERY, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getId());
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(Film film, User user) throws DuplicateEntityException, InternalServerException {
        if (likeCheck(film.getId(), user.getId())) {
            throw new DuplicateEntityException("Пользователь %s уже ставил фильму %s лайк".formatted(user.getId(), film.getId()));
        }
        int likeAdded = jdbc.update(ADD_LIKE_QUERY, film.getId(), user.getId());
        if (likeAdded != 1) {
            throw new InternalServerException("Не удалось добавить лайк");
        }
    }

    @Override
    public void removeLike(Film film, User user) throws InternalServerException {
        int likeDeleted = jdbc.update(DELETE_LIKE_QUERY, film.getId(), user.getId());
        if (likeDeleted != 1) {
            throw new InternalServerException("Не удалось удалить лайк");
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        return Optional.of(jdbc.query(GET_RATED_FILMS_QUERY, ratedMapper, count).getFirst())
                .orElseThrow(() -> new EntityNotFoundException("Не удалось получить популярные фильмы"));
    }

    public boolean likeCheck(Long filmId, Long userId) {
        return jdbc.query(LIKE_CHECK_QUERY, likeCheckMapper, filmId, userId).getFirst();
    }

    public Set<Long> getLikes(Long filmId) {
        List<Set<Long>> query = jdbc.query(GET_LIKES_QUERY, likeMapper, filmId);
        if (query.isEmpty()) {
            return new HashSet<>();
        }
        return query.getFirst();
    }
}