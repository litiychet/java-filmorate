package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private static Long id = 0L;
    private Map<Long, Film> films = new HashMap<>();

    @PostMapping("/films")
    @Validated({Marker.Create.class})
    public Film createFilm(@RequestBody Film film) {
        isValidReleaseDateFilm(film);
        film.setId(++id);
        films.put(id, film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping("/films")
    @Validated({Marker.Update.class})
    public Film updateFilm(@RequestBody Film film) {
        isValidReleaseDateFilm(film);

        Long newFilmId = film.getId();

        if (films.containsKey(newFilmId)) {
            Film replacedFilm = films.get(newFilmId);
            films.put(newFilmId, film);
            log.info("Обновлен фильм: {} на {}", replacedFilm, film);
            return film;
        }

        throw new ValidationException("Такого фильма нет");
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        List<Film> filmsList = new ArrayList<>(films.values());
        return filmsList;
    }

    private void isValidReleaseDateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");
    }
}
