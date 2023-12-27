package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RestController
public class RatingController {
    private RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/mpa/{mpaId}")
    public Rating getRatingById(@PathVariable Long mpaId) {
        log.info("GET /mpa/{}", mpaId);
        return ratingService.getRatingById(mpaId);
    }

    @GetMapping("/mpa")
    public List<Rating> getRatings() {
        log.info("GET /mpa");
        return ratingService.getRatings();
    }
}