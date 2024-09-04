package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllFilms() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user == null) {
            throw new ValidateException("User is null");
        }
        checkValidation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добвален");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new NotFoundException("При запросе нет ID");
        }
        checkValidation(user);
        User userUpdate = users.get(user.getId());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setName(user.getName());
        userUpdate.setBirthday(user.getBirthday());
        userUpdate.setLogin(user.getLogin());
        log.info("Обновили информацию пользователя");
        return userUpdate;
    }

    public void checkValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new ValidateException("Не корректный ввод email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidateException("Поле login не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("Дата рождения введена не корректно");
        }
    }

    private long getNextId() {

        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}