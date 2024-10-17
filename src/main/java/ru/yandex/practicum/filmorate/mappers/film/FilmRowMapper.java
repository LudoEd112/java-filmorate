package ru.yandex.practicum.filmorate.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<List<Film>> {

    @Override
    public List<Film> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<Film> list = new ArrayList<>();
        do {
            Film film = new Film();
            film.setId(resultSet.getLong("id"));
            film.setName(resultSet.getString("name"));
            film.setDescription(resultSet.getString("description"));
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            film.setDuration(Duration.ofSeconds(resultSet.getLong("duration_minutes")));
            list.add(film);
        } while (resultSet.next());
        return list;
    }
}
