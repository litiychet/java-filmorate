package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static Long id = 0L;
    Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        user.setId(++id);
        users.put(id, user);

        return user;
    }

    @Override
    public User updateUser(User user) {
        isValidUser(user);

        String username = user.getName();
        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());

        Long newUserId = user.getId();
        if (users.containsKey(newUserId)) {
            User replacedUser = users.get(newUserId);
            users.put(newUserId, user);
            return user;
        }

        throw new NotFoundException("Пользователь с ID " + newUserId + " не существует");
    }

    @Override
    public List<User> getUsers() {
        List<User> usersList = new ArrayList<>(users.values());
        return usersList;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    private void isValidUser(User user) {
        if (user.getLogin().contains(" "))
            throw new ValidationException("Логин содержит пробел");
    }
}
