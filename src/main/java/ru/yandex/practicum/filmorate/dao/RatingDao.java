package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingDao {
    public Rating getRatingById(Long id);

    public List<Rating> getRatings();
}
