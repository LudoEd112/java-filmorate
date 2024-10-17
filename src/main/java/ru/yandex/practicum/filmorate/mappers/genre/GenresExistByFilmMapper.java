package ru.yandex.practicum.filmorate.mappers.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenresExistByFilmMapper implements RowMapper<List<Long>> {
    @Override
    public List<Long> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<Long> genres = new ArrayList<>();
        do {
            genres.add(resultSet.getLong("genre_id"));
        } while (resultSet.next());
        return genres;
    }
}
