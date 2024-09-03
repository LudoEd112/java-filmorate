package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
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
        User userUpdate = users.get(user.getId());
        userUpdate.setEmail(user.getEmail());
        userUpdate.setName(user.getName());
        userUpdate.setBirthday(user.getBirthday());
        userUpdate.setLogin(user.getLogin());
        log.info("Обновили информацию пользователя");
        return userUpdate;
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