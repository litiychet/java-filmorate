package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    @Validated({Marker.Create.class})
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        log.info("POST /films {}", film);
        log.info("Add film {}", film);
        return film;
    }

    @PutMapping("/films")
    @Validated({Marker.Update.class})
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
        log.info("PUT /films {}", film);
        log.info("Update film {}", film);
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("GET /films OK");
        return filmService.getFilms();
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        log.info("GET /films/{}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLikeFilmByUser(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        log.info("Add like to film {} by user {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLikeByUser(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);
        log.info("Remove like from film {} by user {}", id, userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Long count) {
        log.info("GET /films/popular");
        return filmService.getLastFilms(count);
    }
}
