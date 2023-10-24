package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private static int id = 0;
    private List<Film> films = new ArrayList<>();

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        isValidFilm(film);
        film.setId(++id);
        films.add(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        Integer filmId = film.getId();

        if (filmId == null || filmId <= 0)
            throw new ValidationException("Некорректный ID");

        isValidFilm(film);

        for (Film f : films) {
            if (f.getId() == filmId) {
                films.remove(f);
                films.add(film);
                log.info("Обновлен фильм: {} на {}", f, film);
                return film;
            }
        }

        throw new ValidationException("Такого фильма нет");
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return films;
    }

    private void isValidFilm(Film film) throws ValidationException {
        if (film.getDescription().length() > 200)
            throw new ValidationException("Описание более 200 символов");

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("Дата релиза фильма не может быть раньше 28.12.1895");

        if (film.getDuration() < 0)
            throw new ValidationException("Продолжительность фильма отрицательная");
    }
}
