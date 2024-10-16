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
        while (resultSet.next()) {
            Film iterFilm = new Film();
            iterFilm.setId(resultSet.getLong("id"));
            iterFilm.setName(resultSet.getString("name"));
            iterFilm.setDescription(resultSet.getString("description"));
            iterFilm.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            iterFilm.setDuration(Duration.ofSeconds(resultSet.getLong("duration_minutes")));
            list.add(iterFilm);
        }
        return list;
    }
}
