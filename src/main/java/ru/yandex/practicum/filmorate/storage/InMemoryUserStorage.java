package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static Long id = 0L;
    Map<Long, User> users = new HashMap<>();
    Set<String> emails = new HashSet<>();

    public void resetId() {
        id = 0L;
    }

    @Override
    public User createUser(User user) {
        String username = user.getName();
        String email = user.getEmail();

        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());
        if (emails.contains(email))
            throw new ValidationException("Current email exist...");

        user.setId(++id);
        users.put(id, user);
        emails.add(email);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String username = user.getName();
        String email = user.getEmail();

        if (username == null || username.isEmpty() || username.isBlank())
            user.setName(user.getLogin());
        if (emails.contains(user.getEmail()))
            throw new ValidationException("Current email exist...");

        Long newUserId = user.getId();
        if (users.containsKey(newUserId)) {
            users.put(newUserId, user);
            emails.add(email);
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
}
