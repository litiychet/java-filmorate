package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Qualifier("memoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private static Long id = 0L;
    private Map<Long, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

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
            emails.remove(users.get(newUserId).getEmail());
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

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (getUserById(userId).getFriendsList().contains(friendId))
            throw new ValidationException("Пользователь с ID " + userId + " уже отправил запрос дружбы пользователю " + friendId);
        getUserById(userId).getFriendsList().add(friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!getUserById(userId).getFriendsList().contains(friendId))
            throw new ValidationException("У пользователя с ID " + userId + " нет в друзьях пользователя " + friendId);
        getUserById(userId).getFriendsList().remove(friendId);
    }
}
