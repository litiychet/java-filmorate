package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    @Validated({Marker.Create.class})
    public Film createFilm(@RequestBody Film film) {
        filmService.createFilm(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping("/films")
    @Validated({Marker.Update.class})
    public Film updateFilm(@RequestBody Film film) {
        filmService.updateFilm(film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeFilmByUser(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Long count) {
        return filmService.getLastFilms(count);
    }
}
