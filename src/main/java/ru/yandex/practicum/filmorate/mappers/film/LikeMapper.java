package ru.yandex.practicum.filmorate.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class LikeMapper implements RowMapper<Set<Long>> {

    @Override
    public Set<Long> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        HashSet<Long> likes = new HashSet<>();
        do {
            likes.add(resultSet.getLong("user_id"));
        } while (resultSet.next());
        return likes;
    }
}
