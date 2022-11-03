package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Добавление пользователя {}", user);
        if (users.containsKey(user.getId())) {
            String message = "Пользователь с таким id уже существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(user);
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление пользователя {}", user);
        if (!users.containsKey(user.getId())) {
            String message = "Пользователя с таким id не существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validate(User user) {
        if (user.getLogin().contains(" ")) {
            String message = "Неправильный формат логина.";
            log.error(message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя не передано, вместо имени установлен переданный логин");
            user.setName(user.getLogin());
        }
    }
}
