package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private Validator validator;

    @Autowired
    public UserService(UserStorage userStorage, Validator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public User createUser(User user) {
        userValidation(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        userValidation(user);
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

        List<User> commonFriendsList = new ArrayList<>();

        for (Long id1 : userStorage.getUserById(userId).getFriendsList()) {
            for (Long id2 : userStorage.getUserById(friendId).getFriendsList()) {
                if (id1 == id2)
                    commonFriendsList.add(userStorage.getUserById(id1));
            }
        }

        return commonFriendsList;
    }

    private void userValidation(User user) {
        Set<ConstraintViolation<User>> violation = validator.validate(user);
        if (!violation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<User> constraintViolation : violation) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violation);
        }
    }
}
