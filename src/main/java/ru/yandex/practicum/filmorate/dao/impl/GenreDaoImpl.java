package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"genre\" WHERE \"id\" = ?", id);

        if (genreRows.next()) {
            Genre genre = new Genre();

            genre.setId(genreRows.getLong("id"));
            genre.setName(genreRows.getString("name"));

            return genre;
        } else {
            return null;
        }
    }

    public List<Genre> getGenres() {
        return jdbcTemplate.query(
                "SELECT * FROM \"genre\"",
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }
        );
    }
}
