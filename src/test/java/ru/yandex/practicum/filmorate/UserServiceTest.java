package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static UserStorage userStorage = new InMemoryUserStorage();
    static UserService userService = new UserService(userStorage);

    @BeforeAll
    public static void setUp() {
        User user = new User();
        user.setEmail("somemail1@yandex.ru");
        user.setName("user1");
        user.setLogin("login1");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        User user2 = new User();
        user2.setEmail("somemail2@yandex.ru");
        user2.setName("user2");
        user2.setLogin("login2");
        user2.setBirthday(LocalDate.of(2001, 7, 5));

        User user3 = new User();
        user3.setEmail("somemail3@yandex.ru");
        user3.setName("user3");
        user3.setLogin("login3");
        user3.setBirthday(LocalDate.of(2001, 7, 5));

        userService.createUser(user);
        userService.createUser(user2);
        userService.createUser(user3);

        userService.addFriend(2L, 3L);
        userService.addFriend(4L, 3L);
    }

    @Test
    public void createUserWithEmptyName() {
        User user = new User();
        user.setEmail("somemail4@yandex.ru");
        user.setLogin("login4");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        userService.createUser(user);

        assertEquals("login4", userService.getUserById(5L).getName());
    }

    @Test
    public void createUserWithLoginWithSpaces() {
        User user = new User();
        user.setEmail("somemail5@yandex.ru");
        user.setLogin("login 5");
        user.setName("name5");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        Exception exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("Логин содержит пробел", exception.getMessage());
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("update@yandex.ru");
        user.setLogin("updateLogin");
        user.setName("update name");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        userService.updateUser(user);

        assertEquals("updateLogin", userService.getUserById(2L).getLogin());
    }

    @Test
    public void getUsers() {
        assertEquals(4, userService.getUsers().size());
    }

    @Test
    public void getUserById() {
        assertEquals("login2", userService.getUserById(3L).getLogin());
    }

    @Test
    public void addFriend() {
        userService.addFriend(2L, 3L);
        assertFalse(userService.getUserFriends(2L).isEmpty());
    }

    @Test
    public void addNotExistFriend() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.addFriend(2L, 99L));
        assertEquals("Пользователя с ID 99 не существует", exception.getMessage());
    }

    @Test
    public void addFriendNotExitsUser() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.addFriend(99L, 2L));
        assertEquals("Пользователя с ID 99 не существует", exception.getMessage());
    }

    @Test
    public void getUserFriends() {
        assertFalse(userService.getUserFriends(2L).isEmpty());
    }

    @Test
    public void getUserFriendsFromNotExistUser() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.getUserFriends(99L));
        assertEquals("Пользователя с ID 99 не существует", exception.getMessage());
    }

    @Test
    public void getCommonFriends() {
        assertFalse(userService.getCommonFriendsList(2L, 4L).isEmpty());
    }

    @Test
    public void getCommonFriendsOnNotExistUser() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.getCommonFriendsList(99L, 3L));
        assertEquals("Пользователя с ID 99 не существует", exception.getMessage());
    }
}
