package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        checkUserExists(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        checkUserExists(id);
        User user = userStorage.getUserById(id);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(Long userId) {
        checkUserExists(userId);

        List<User> friendsList = userStorage.getUserById(userId).getFriendsList().stream()
                .map(u -> userStorage.getUserById(u))
                .collect(Collectors.toList());

        return friendsList;
    }

    public List<User> getCommonFriendsList(Long userId, Long friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);

        List<User> commonFriendsList = userStorage.getUserById(userId).getFriendsList().stream()
                .filter((u) -> userStorage.getUserById(friendId).getFriendsList().contains(u))
                .map(u -> userStorage.getUserById(u))
                .collect(Collectors.toList());

        return commonFriendsList;
    }

    private void checkUserExists(Long id) {
        if (userStorage.getUserById(id) == null)
            throw new NotFoundException("Пользователя с ID " + id + " не существует");
    }
}
