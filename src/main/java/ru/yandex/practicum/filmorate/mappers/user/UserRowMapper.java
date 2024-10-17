package ru.yandex.practicum.filmorate.mappers.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRowMapper implements RowMapper<Map<Long, User>> {
    @Override
    public Map<Long, User> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Map<Long, User> map = new HashMap<>();
        do {
            Long userId = resultSet.getLong("id");
            User user = map.getOrDefault(userId, new User());
            if (user.getId() == null) {
                user.setId(resultSet.getLong("id"));
                user.setLogin(resultSet.getString("login"));
                user.setEmail(resultSet.getString("email"));
                user.setName(resultSet.getString("name"));
                user.setBirthday(resultSet.getDate("birthdate").toLocalDate());
            }
            long friendId = resultSet.getLong("friend_added_id");
            if (friendId != 0) {
                user.getFriends().add(friendId);
            }
            map.put(userId, user);
        } while (resultSet.next());

        return map;
    }
}
