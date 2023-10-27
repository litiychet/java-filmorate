package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    static UserController userController = new UserController();
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateUserSuccess() {
        User user = new User("somemail@yandex.ru", "login", LocalDate.of(2001, 7, 5));
        user.setName("name");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateUserWithEmptyName() {
        User user = new User("somemail@yandex.ru", "login", LocalDate.of(2001, 7, 5));
        User newUser = userController.createUser(user);
        assertEquals("login", newUser.getName());
    }

    @Test
    void validateUserWithIncorrectEmail() {
        User user = new User("somemail.ru", "login", LocalDate.of(2001, 7, 5));
        user.setName("name");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a well-formed email address", c.getMessage());
    }

    @Test
    void validateUserWithLoginWithSpace() {
        User user = new User("somemail@yandex.ru", "log in", LocalDate.of(2001, 7, 5));
        user.setName("name");

        Exception exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин содержит пробел", exception.getMessage());
    }

    @Test
    void validateUserWithLoginOnlySpaces() {
        User user = new User("somemail@yandex.ru", "    ", LocalDate.of(2001, 7, 5));
        user.setName("name");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must not be blank", c.getMessage());
    }

    @Test
    void validateUserWithFutureBirthdate() {
        User user = new User("somemail@yandex.ru", "login", LocalDate.of(2148, 7, 5));
        user.setName("name");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a past date", c.getMessage());
    }

    @Test
    void validateUserWithBorderBirthDate() {
        User user = new User("somemail@yandex.ru", "login", LocalDate.now());
        user.setName("name");

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a past date", c.getMessage());
    }
}
