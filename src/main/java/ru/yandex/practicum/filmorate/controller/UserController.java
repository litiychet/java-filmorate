package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @Validated({Marker.Create.class})
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        log.info("POST /users {}", user);
        log.info("Add user {}", user);
        return user;
    }

    @PutMapping("/users")
    @Validated({Marker.Update.class})
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.info("PUT /users {}", user);
        log.info("Update user {}", user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        log.info("GET /users OK");
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("GET /users/{}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT /users/{}/friends/{}", id, friendId);
        log.info("Add user {} to friend by user {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);
        log.info("Remove friend {} by user {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        log.info("GET /users/{}/friends", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonUsersFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("GET /users/{}/friends/common/{}", id, otherId);
        return userService.getCommonFriendsList(id, otherId);
    }
}
