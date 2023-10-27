package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {
    static FilmController filmController = new FilmController();
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validateFilmSuccess() {
        final Film film  = new Film("Film name", "Description", LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateFilmWithEmptyName() {
        final Film film = new Film("", "Description", LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must not be blank", c.getMessage());
    }

    @Test
    void validateFilmWithIncorrectDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++)
            stringBuilder.append('a');

        final Film film = new Film("Name", stringBuilder.toString(), LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("size must be between 0 and 200", c.getMessage());
    }

    @Test
    void validateFilmEmptyDescription() {
        final Film film = new Film("Name", "", LocalDate.now(), 100);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void validateFilmWithIncorrectReleaseDate() {
        final Film film = new Film("Name", "Description", LocalDate.of(1795, 12, 1), 100);
        Exception exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза фильма не может быть раньше 28.12.1895", exception.getMessage());
    }

    @Test
    void validateFilmWithBorderReleaseDate() {
        final Film film = new Film("Name", "Description", LocalDate.of(1895, 12, 28), 100);
        assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void validateFilmWithNegativeDuration() {
        final Film film = new Film("Name", "Description", LocalDate.now(), -100);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must be greater than or equal to 1", c.getMessage());
    }

    @Test
    void validateFilmWithZeroDuration() {
        final Film film = new Film("Name", "Description", LocalDate.now(), 0);
        Set<ConstraintViolation<Film>> constraintViolations = validator.validate(film);
        for (ConstraintViolation<Film> c : constraintViolations)
            assertEquals("must be greater than or equal to 1", c.getMessage());
    }
}
