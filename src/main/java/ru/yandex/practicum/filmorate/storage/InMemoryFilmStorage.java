package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("memoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static Long id = 0L;
    private Map<Long, Film> films = new HashMap<>();

    public void resetId() {
        id = 0L;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(++id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long newFilmId = film.getId();


        if (films.containsKey(newFilmId)) {
            Film replacedFilm = films.get(newFilmId);
            films.put(newFilmId, film);
            return film;
        }

        throw new NotFoundException("Фильма с ID " + newFilmId + " не существует");
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmsList = new ArrayList<>(films.values());
        return filmsList;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

    @Override
    public void addUserLike(Long userId, Long filmId) {
        getFilmById(filmId).getUsersLikes().add(userId);
    }

    @Override
    public void removeUserLike(Long userId, Long filmId) {
        getFilmById(filmId).getUsersLikes().remove(userId);
    }
}
