
package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        log.info("Создание пользователя: {}", user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %d не найден".formatted(id)));
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %d не найден".formatted(userId)));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг с ID: %d не найден".formatted(friendId)));
        log.debug("Добавление пользователя {} в друг к {}", user, friend);
        userStorage.addToFriends(user, friend);
        log.debug("Пользователь {} добавлен в друзья к {}", user, friend);
        return friend;
    }

    public User removeFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %d не найден".formatted(userId)));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с ID: %d не найден".formatted(friendId)));
        log.debug("Удаление пользователя {} из друзей {}", user, friend);
        userStorage.removeFromFriends(user, friend);
        log.debug("Пользователь {} удален из друзья {}", user, friend);
        return friend;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID: %d не найден".formatted(userId)));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new NotFoundException("Друг с ID: %d не найден".formatted(friendId)));
        log.info("Получение взаимных друзей пользователя {} от друга {}", user, friend);
        return userStorage.getMutualFriends(user, friend);
    }

    public Collection<User> getAllFriends(long userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с ID: %d не найден".formatted(userId)));
        return userStorage.getFriendsList(user);
    }
}
