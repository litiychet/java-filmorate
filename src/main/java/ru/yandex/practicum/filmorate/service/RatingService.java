package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Service
public class RatingService {
    private RatingDao ratingDao;

    @Autowired
    public RatingService(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public Rating getRatingById(Long ratingId) {
        if (ratingDao.getRatingById(ratingId) == null)
            throw new NotFoundException("MPA рейтинга с ID " + ratingId + " не существует");
        return ratingDao.getRatingById(ratingId);
    }

    public List<Rating> getRatings() {
        return ratingDao.getRatings();
    }
}
