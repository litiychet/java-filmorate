package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static Long id = 0L;
    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        isValidReleaseDateFilm(film);
        film.setId(++id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        isValidReleaseDateFilm(film);

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

    private void isValidReleaseDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
    }
}
