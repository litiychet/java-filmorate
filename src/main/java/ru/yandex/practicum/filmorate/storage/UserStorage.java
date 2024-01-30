package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public void resetId();

    public User createUser(User user);

    public User updateUser(User user);

    public List<User> getUsers();

    public User getUserById(Long id);

    public void addFriend(Long userId, Long friendId);

    public void removeFriend(Long userId, Long friendId);
}
