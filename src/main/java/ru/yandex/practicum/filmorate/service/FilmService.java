package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Validator validator;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, Validator validator) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.validator = validator;
    }

    public Film createFilm(Film film) {
        filmValidation(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        if (filmStorage.getFilmById(filmId) == null)
            throw new NotFoundException("Фильма c ID " + filmId + " не существует");
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long userId, Long filmId) {
        if (filmStorage.getFilmById(filmId) == null)
            throw new NotFoundException("Фильма с ID " + filmId + " не существует");
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
        filmStorage.getFilmById(filmId).getUsersLikes().add(userId);
    }

    public void removeLike(Long userId, Long filmId) {
        if (filmStorage.getFilmById(filmId) == null)
            throw new NotFoundException("Фильма с ID " + filmId + " не существует");
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
        filmStorage.getFilmById(filmId).getUsersLikes().remove(userId);
    }

    public List<Film> getLastFilms(Long size) {
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) ->
                    Integer.valueOf(f1.getUsersLikes().size()).compareTo(f2.getUsersLikes().size())
                )
                .limit(size)
                .collect(Collectors.toList());
    }

    private void filmValidation(Film film) {
        Set<ConstraintViolation<Film>> violation = validator.validate(film);
        if (!violation.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Film> constraintViolation : violation) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb.toString(), violation);
        }
    }
}