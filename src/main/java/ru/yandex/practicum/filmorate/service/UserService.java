package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null)
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователя с ID " + friendId + " не существует");
        }
        userStorage.getUserById(userId).getFriendsList().add(friendId);
        userStorage.getUserById(friendId).getFriendsList().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователя с ID " + friendId + " не существует");
        }
        userStorage.getUserById(userId).getFriendsList().remove(friendId);
        userStorage.getUserById(friendId).getFriendsList().remove(userId);
    }

    public List<User> getUserFriends(Long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");

        List<User> friendsList = new ArrayList<>();

        for (Long id : userStorage.getUserById(userId).getFriendsList()) {
            if (userStorage.getUserById(id) != null)
                friendsList.add(userStorage.getUserById(id));
        }

        return friendsList;
    }

    public List<User> getCommonFriendsList(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователя с ID " + friendId + " не существует");
        }

        List<User> commonFriendsList = userStorage.getUserById(userId).getFriendsList().stream()
                .filter((u) -> userStorage.getUserById(friendId).getFriendsList().contains(u))
                .map(u -> userStorage.getUserById(u))
                .collect(Collectors.toList());

        return commonFriendsList;
    }
}
