package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.mpa.MpaExistMapper;
import ru.yandex.practicum.filmorate.mappers.mpa.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaMapper mapper;
    private final MpaExistMapper existMapper;
    private static final String SQL_GET_ALL_RATINGS = "SELECT * FROM rating;";
    private static final String SQL_GET_RATING_BY_ID = "SELECT * FROM rating WHERE rating_id = ?;";
    private static final String SQL_UPDATE_FILM_RATING = "UPDATE films SET rating_id = ? WHERE id = ?;";
    private static final String SQL_GET_RATING_BY_FILM_ID = "SELECT r.RATING_ID, r.NAME FROM RATING r RIGHT JOIN FILMS f ON r.RATING_ID = f.RATING_ID WHERE f.ID = ?;";
    private static final String SQL_UPDATE_RATING_BY_ID = "UPDATE rating SET name = ? WHERE rating_id = ?;";
    private static final String SQL_DELETE_RATING_BY_ID = "DELETE FROM rating WHERE rating_id = ?;";
    private static final String SQL_CHECK_RATING_EXIST_BY_NAME = "SELECT COUNT(*) AS exist FROM rating WHERE name = ?;";
    private static final String SQL_CHECK_RATING_EXIST_BY_ID = "SELECT COUNT(*) AS exist FROM rating WHERE rating_id = ?;";

    public Mpa getMpa(Long filmId) {
        return jdbc.query(SQL_GET_RATING_BY_FILM_ID, mapper, filmId).getFirst().getFirst();
    }

    public Mpa getMpaById(Long mpaId) {
        return jdbc.query(SQL_GET_RATING_BY_ID, mapper, mpaId).getFirst().getFirst();
    }

    public boolean addMpa(Mpa mpa, Long filmId) {
        return jdbc.update(SQL_UPDATE_FILM_RATING, mpa.getId(), filmId) > 0;
    }

    public Collection<Mpa> findAll() {
        return jdbc.query(SQL_GET_ALL_RATINGS, mapper).getFirst();
    }

    public Long create(Mpa mpa) {
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("name", mpa.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("rating")
                .usingGeneratedKeyColumns("rating_id");

        return simpleJdbcInsert.executeAndReturnKey(ratingMap).longValue();
    }

    public Mpa update(Mpa mpa) {
        jdbc.update(SQL_UPDATE_RATING_BY_ID, mpa.getName(), mpa.getId());
        return getMpa(mpa.getId());
    }

    public boolean delete(Long id) {
        return jdbc.update(SQL_DELETE_RATING_BY_ID, id) > 0;
    }

    public boolean isMpaExist(String name) {
        return !jdbc.query(SQL_CHECK_RATING_EXIST_BY_NAME, existMapper, name).isEmpty();
    }

    public boolean isMpaExistId(Long id) {
        return jdbc.query(SQL_CHECK_RATING_EXIST_BY_ID, existMapper, id).getFirst();
    }
}
