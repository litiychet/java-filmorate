package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateFilmSuccess() {
        final Film film  = new Film();
        film.setName("Film name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateFilmWithEmptyName() {
        final Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must not be blank", c.getMessage());
    }

    @Test
    void validateFilmWithIncorrectDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++)
            stringBuilder.append('a');

        final Film film = new Film();
        film.setName("Name");
        film.setDescription(stringBuilder.toString());
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("size must be between 0 and 200", c.getMessage());
    }

    @Test
    void validateFilmEmptyDescription() {
        final Film film = new Film();
        film.setName("Name");
        film.setDescription("");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateFilmWithNegativeDuration() {
        final Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-100);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must be greater than or equal to 1", c.getMessage());
    }

    @Test
    void validateFilmWithZeroDuration() {
        final Film film = new Film();
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0);

        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must be greater than or equal to 1", c.getMessage());
    }
}
