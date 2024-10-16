package ru.yandex.practicum.filmorate.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeCheckMapper implements RowMapper<Boolean> {
    @Override
    public Boolean mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return (resultSet.getLong("likes") > 0);
    }
}
