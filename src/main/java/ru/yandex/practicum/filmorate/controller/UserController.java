package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private static Long id = 0L;
    Map<Long, User> users = new HashMap<>();

    @PostMapping("/users")
    @Validated({Marker.Create.class})
    public User createUser(@Valid @RequestBody User user) {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        user.setId(++id);
        users.put(id, user);
        log.info("Добавлен польозватель: {}", user);

        return user;
    }

    @PutMapping("/users")
    @Validated({Marker.Update.class})
    public User updateUser(@Valid @RequestBody User user) {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        Long newUserId = user.getId();
        if (users.containsKey(newUserId)) {
            User replacedUser = users.get(newUserId);
            users.put(newUserId, user);
            log.info("Изменен пользователь: {} на {}", replacedUser, user);
            return user;
        }

        throw new ValidationException("Такого пользователя нет");
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        return usersList;
    }

    private void isValidUser(User user) {
        if (user.getLogin().contains(" "))
            throw new ValidationException("Логин содержит пробел");
    }
}
