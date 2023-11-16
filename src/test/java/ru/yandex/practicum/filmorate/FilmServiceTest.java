package ru.yandex.practicum.filmorate;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    static FilmStorage filmStorage = new InMemoryFilmStorage();
    static UserStorage userStorage = new InMemoryUserStorage();
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    static FilmService filmService = new FilmService(filmStorage, userStorage, validator);

    @BeforeAll
    public static void setUp() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        Film film2 = new Film();
        film2.setName("Film 2 name");
        film2.setDescription("Description");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(100);

        Film film3 = new Film();
        film3.setName("Film 3 name");
        film3.setDescription("Description");
        film3.setReleaseDate(LocalDate.now());
        film3.setDuration(100);

        User user = new User();
        user.setEmail("somemail@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2001, 7, 5));

        filmService.createFilm(film);
        filmService.createFilm(film2);
        filmService.createFilm(film3);
        userStorage.createUser(user);
    }

    @Test
    public void createFilmWithIncorrectReleaseDate() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1792, 12, 1));
        film.setDuration(100);

        Exception exception = assertThrows(ValidationException.class, () -> filmService.createFilm(film));
        assertEquals("Дата релиза фильма не может быть раньше 28.12.1895", exception.getMessage());
    }

    @Test
    public void updateFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Update film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        filmService.updateFilm(film);

        assertEquals("Update film", filmService.getFilmById(1L).getName());
        assertEquals(10, filmService.getFilmById(1L).getDuration());
    }

    @Test
    public void getFilms() {
        assertEquals(3, filmService.getFilms().size());
    }

    @Test
    public void addLike() {
        filmService.addLike(5L, 1L);

        assertEquals(1, filmService.getFilmById(1L).getUsersLikes().size());
    }

    @Test
    public void removeLike() {
        filmService.removeLike(5L, 1L);

        assertEquals(0, filmService.getFilmById(1L).getUsersLikes().size());
    }

    @Test
    public void addLikeByNotExistingUser() {
        Exception exception = assertThrows(NotFoundException.class, () -> filmService.addLike(2L, 1L));
        assertEquals("Пользователя с ID 2 не существует", exception.getMessage());
    }

    @Test
    public void removeLikeByNotExistUser() {
        Exception exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(2L, 1L));
        assertEquals("Пользователя с ID 2 не существует", exception.getMessage());
    }

    @Test
    public void addLikeToNotExistFilm() {
        Exception exception = assertThrows(NotFoundException.class, () -> filmService.addLike(1L, 99L));
        assertEquals("Фильма с ID 99 не существует", exception.getMessage());
    }

    @Test
    public void removeLikeToNotExistFilm() {
        Exception exception = assertThrows(NotFoundException.class, () -> filmService.removeLike(1L, 99L));
        assertEquals("Фильма с ID 99 не существует", exception.getMessage());
    }

    @Test
    public void getPopularFilmsList() {
        assertEquals(1, filmService.getLastFilms(1L).size());
    }
}
