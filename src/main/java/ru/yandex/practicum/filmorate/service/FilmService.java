package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage, @Qualifier("dbUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        isValidReleaseDateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        checkFilmExists(film.getId());
        isValidReleaseDateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long filmId) {
        checkFilmExists(filmId);
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Long userId, Long filmId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.addUserLike(userId, filmId);
    }

    public void removeLike(Long userId, Long filmId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.removeUserLike(userId, filmId);
    }

    public List<Film> getLastFilms(Long size) {
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) ->
                    Integer.valueOf(f1.getUsersLikes().size()).compareTo(f2.getUsersLikes().size())
                )
                .limit(size)
                .collect(Collectors.toList());
    }

    private void isValidReleaseDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
    }

    private void checkFilmExists(Long filmId) {
        if (filmStorage.getFilmById(filmId) == null)
            throw new NotFoundException("Фильма с ID " + filmId + " не существует");
    }

    private void checkUserExists(Long userId) {
        if (userStorage.getUserById(userId) == null)
            throw new NotFoundException("Пользователя с ID " + userId + " не существует");
    }
}