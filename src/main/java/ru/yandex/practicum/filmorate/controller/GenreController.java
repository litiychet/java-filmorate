package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
public class GenreController {
    private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenreById(@PathVariable Long genreId) {
        log.info("GET /genres/{}", genreId);
        return genreService.getGenreById(genreId);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("GET /genres/");
        return genreService.getGenres();
    }
}
