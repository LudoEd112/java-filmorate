package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(long id);

    User createUser(User user) throws InternalServerException;

    User updateUser(User user) throws InternalServerException;

    void addToFriends(User user, User friend);

    void removeFromFriends(User user, User friend);

    List<Long> getMutualFriends(User user, User friend);

    List<Long> getFriendsList(User user);
}