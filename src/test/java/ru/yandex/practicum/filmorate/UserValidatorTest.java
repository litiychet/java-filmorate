package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    @Mock
    static UserService userService;
    static UserController userController = new UserController(userService);
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateUserSuccess() {
        User user = new User();
        user.setEmail("somemail@yandex.ru");
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateUserWithIncorrectEmail() {
        User user = new User();
        user.setEmail("somemail.ru");
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a well-formed email address", c.getMessage());
    }

    @Test
    void validateUserWithLoginOnlySpaces() {
        User user = new User();
        user.setEmail("somemail@yandex.ru");
        user.setName("name");
        user.setLogin("    ");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must not be blank", c.getMessage());
    }

    @Test
    void validateUserWithFutureBirthdate() {
        User user = new User();
        user.setEmail("somemail@yandex.ru");
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2148, 7, 5));

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a past date", c.getMessage());
    }

    @Test
    void validateUserWithBorderBirthDate() {
        User user = new User();
        user.setEmail("somemail@yandex.ru");
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> c : constraintViolations)
            assertEquals("must be a past date", c.getMessage());
    }
}
