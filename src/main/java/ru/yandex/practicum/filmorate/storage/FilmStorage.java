package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public void resetId();

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getFilms();

    public Film getFilmById(Long id);

    public void addUserLike(Long userId, Long filmId);

    public void removeUserLike(Long userId, Long filmId);
}
