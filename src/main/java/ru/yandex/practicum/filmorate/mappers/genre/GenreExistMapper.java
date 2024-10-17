package ru.yandex.practicum.filmorate.mappers.genre;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreExistMapper implements RowMapper<Boolean> {
    @Override
    public Boolean mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("genre_exist") > 0;
    }
}
