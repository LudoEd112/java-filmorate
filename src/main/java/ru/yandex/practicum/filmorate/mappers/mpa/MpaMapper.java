package ru.yandex.practicum.filmorate.mappers.mpa;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaMapper implements RowMapper<List<Mpa>> {
    @Override
    public List<Mpa> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<Mpa> ratings = new ArrayList<>();
        do {
            ratings.add(new Mpa(resultSet.getLong("rating_id"), resultSet.getString("name")));
        } while (resultSet.next());
        return ratings;
    }
}
