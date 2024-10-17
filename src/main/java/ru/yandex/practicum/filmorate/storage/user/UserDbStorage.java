package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.user.FriendListMapper;
import ru.yandex.practicum.filmorate.mappers.user.MutualFriendsMapper;
import ru.yandex.practicum.filmorate.mappers.user.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private final FriendListMapper friendsMapper;
    private final MutualFriendsMapper mutualFriendsMapper;
    private static final String ERROR_USER_NOT_FOUND = "Пользователь с id %s не найден";
    private static final String SQL_UPDATE_USER_BY_ID = "UPDATE users SET email = ?, login = ?, name = ?, birthdate = ? WHERE id = ?;";
    private static final String SQL_FIND_ALL_USERS = "SELECT u.*, f.FRIEND_ADDED_ID FROM users u LEFT JOIN user_friend f ON u.id = f.user_id;";
    private static final String SQL_FIND_USER_BY_ID = "SELECT u.*, f.FRIEND_ADDED_ID FROM users u LEFT JOIN user_friend f ON u.id = f.user_id WHERE id = ?;";
    private static final String SQL_INSERT_NEW_FRIEND = "INSERT INTO user_friend(user_id, friend_added_id) VALUES (?, ?);";
    private static final String SQL_GET_USER_FRIENDS = "SELECT user_id AS id FROM user_friend WHERE friend_added_id = ?;";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM user_friend WHERE user_id = ? AND friend_added_id = ?;";
    private static final String SQL_GET_MUTUAL_FRIENDS = """
            SELECT uf1.user_id AS mutual_friends
            FROM user_friend uf1 JOIN user_friend uf2 ON uf1.user_id = uf2.user_id
            WHERE uf1.friend_added_id = ? AND uf2.friend_added_id = ?;""";
    private static final String SQL_INSERT_USER = "INSERT INTO USERS (EMAIL , LOGIN , NAME , BIRTHDATE) VALUES (?, ?, ?, ?)";

    @Override
    public Collection<User> getAllUsers() {
        return Optional.of(jdbc.query(SQL_FIND_ALL_USERS, mapper).getFirst().values())
                .orElseThrow(() -> new EntityNotFoundException("Не удалось получить пользователей"));
    }

    @Override
    public User getUserById(long id) {
        if (jdbc.queryForObject(SQL_FIND_USER_BY_ID, mapper, id) == null) {
            throw new EntityNotFoundException(ERROR_USER_NOT_FOUND.formatted(id));
        }
        return jdbc.queryForObject(SQL_FIND_USER_BY_ID, mapper, id).get(id);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                SQL_INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps; }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public User updateUser(User user) {
        int rowsUpdated = jdbc.update(SQL_UPDATE_USER_BY_ID, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsUpdated == 0) {
            throw new NotFoundException("Не удалось обновить данные");
        }
        return user;
    }

    @Override
    public void addToFriends(User user, User friend) {
        jdbc.update(SQL_INSERT_NEW_FRIEND, friend.getId(), user.getId());
    }

    @Override
    public void removeFromFriends(User user, User friend) {
        jdbc.update(SQL_DELETE_FRIEND, friend.getId(), user.getId());
    }

    @Override
    public List<Long> getMutualFriends(User user, User friend) {
        List<List<Long>> friends = jdbc.query(SQL_GET_MUTUAL_FRIENDS, mutualFriendsMapper, friend.getId(), user.getId());
        if (friends.isEmpty()) {
            return new ArrayList<>();
        }
        return friends.getFirst();
    }

    @Override
    public List<Long> getFriendsList(User user) {
        List<ArrayList<Long>> query = jdbc.query(SQL_GET_USER_FRIENDS, friendsMapper, user.getId());
        if (!query.isEmpty()) {
            return query.getFirst();
        }
        return new ArrayList<>();
    }
}