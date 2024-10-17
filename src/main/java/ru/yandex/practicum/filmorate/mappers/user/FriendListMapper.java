package ru.yandex.practicum.filmorate.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class FriendListMapper implements RowMapper<ArrayList<Long>> {
    @Override
    public ArrayList<Long> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ArrayList<Long> friends = new ArrayList<>();
        do {
            friends.add(resultSet.getLong("id"));
        } while (resultSet.next());
        return friends;
    }
}
