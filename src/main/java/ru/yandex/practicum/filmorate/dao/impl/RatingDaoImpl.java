package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

@Component
public class RatingDaoImpl implements RatingDao {
    private JdbcTemplate jdbcTemplate;

    public RatingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Rating getRatingById(Long id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"rating\" WHERE \"id\" = ?", id);

        if (ratingRows.next()) {
            Rating rating = new Rating();

            rating.setId(ratingRows.getLong("id"));
            rating.setName(ratingRows.getString("name"));

            return rating;
        } else {
            return null;
        }
    }

    public List<Rating> getRatings() {
        return jdbcTemplate.query(
                "SELECT * FROM \"rating\"",
                (rs, rowNum) -> {
                    Rating rating = new Rating();
                    rating.setId(rs.getLong("id"));
                    rating.setName(rs.getString("name"));
                    return rating;
                }
        );
    }
}
