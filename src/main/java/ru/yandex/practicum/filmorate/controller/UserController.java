package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private static int id = 0;
    List<User> users = new ArrayList<>();
    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        user.setId(++id);
        users.add(user);
        log.info("Добавлен польозватель: {}", user);

        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        for (User u : users) {
            if (u.getId() == user.getId()) {
                users.remove(u);
                users.add(user);
                log.info("Изменен пользователь: {} на {}", u, user);
                return user;
            }
        }
        throw new ValidationException("Такого пользователя нет");
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    private void isValidUser(User user) throws ValidationException {
        if (user.getLogin().contains(" "))
            throw new ValidationException("Логин содержит пробел");

        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения не может быть в будущем");
    }
}
