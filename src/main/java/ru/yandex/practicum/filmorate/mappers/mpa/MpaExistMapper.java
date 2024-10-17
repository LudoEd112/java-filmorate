package ru.yandex.practicum.filmorate.mappers.mpa;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaExistMapper implements RowMapper<Boolean> {
    @Override
    public Boolean mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("exist") > 0;
    }
}
