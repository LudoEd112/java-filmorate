package ru.yandex.practicum.filmorate.mappers.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenresMapper implements RowMapper<List<Genre>> {
    @Override
    public List<Genre> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        do {
            genres.add(new Genre(resultSet.getLong("genre_id"), resultSet.getString("name")));
        } while (resultSet.next());
        return genres;
    }
}
