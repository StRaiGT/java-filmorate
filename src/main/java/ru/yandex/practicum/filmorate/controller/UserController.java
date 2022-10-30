package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {
    @GetMapping
    public Collection<User> getAll() {
        return items.values();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Добавление пользователя {}", user);
        validate(user);
        user.setId(id++);
        items.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновление пользователя {}", user);
        if (!items.containsKey(user.getId())) {
            String message = "Пользователя с таким id не существует.";
            log.error(message);
            throw new ValidationException(message);
        }
        validate(user);
        items.put(user.getId(), user);
        return user;
    }

    protected void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Неправильный формат логина.";
            log.error(message);
            throw new ValidationException(message);
        }
    }
}
