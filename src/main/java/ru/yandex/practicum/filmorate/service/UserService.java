package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserDbStorage userStorage;

    public User createUser(User user) throws InternalServerException {
        log.info("Создание пользователя: {}", user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) throws InternalServerException {
        log.info("Обновление пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(long userId, long friendId) {
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Пользователя с Id %d не существует".formatted(userId));
        }
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(friendId)) {
            throw new NotFoundException("Пользователя с Id %d не существует".formatted(friendId));
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.debug("Добавление пользователя {} в друг к {}", user, friend);
        userStorage.addToFriends(user, friend);
        log.debug("Пользователь {} добавлен в друзья к {}", user, friend);
        return friend;
    }

    public User removeFriend(long userId, long friendId) {
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Пользователя с Id %d не существует".formatted(userId));
        }
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(friendId)) {
            throw new NotFoundException("Пользователя с Id %d не существует".formatted(friendId));
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.debug("Удаление пользователя {} из друзей {}", user, friend);
        userStorage.removeFromFriends(user, friend);
        log.debug("Пользователь {} удален из друзья {}", user, friend);
        return friend;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Collection<User> getUsersById(List<Long> friends) {
        return friends.stream().map(userStorage::getUserById).toList();
    }

    public Collection<User> getMutualFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.info("Получение взаимных друзей пользователя {} от друга {}", user, friend);
        return getUsersById(userStorage.getMutualFriends(user, friend));
    }

    public Collection<User> getAllFriends(long userId) {
        if (!userStorage.getAllUsers().stream().map(User::getId).toList().contains(userId)) {
            throw new NotFoundException("Пользователя с Id %d не существует".formatted(userId));
        }
        User user = userStorage.getUserById(userId);
        return getUsersById(userStorage.getFriendsList(user));
    }
}
