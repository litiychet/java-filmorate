package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("dbFilmStorage")
public class FilmDao implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final RatingDao ratingDao;

    public void resetId() {

    }

    @Autowired
    public FilmDao(JdbcTemplate jdbcTemplate, GenreDao genreDao, RatingDao ratingDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.ratingDao = ratingDao;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String query = "INSERT INTO \"films\" " +
                "(\"name\"," +
                "\"description\"," +
                "\"release_date\"," +
                "\"duration\"," +
                "\"rating_id\") " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());

            return ps;
        }, keyHolder);

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO \"film_genre\" " +
                                "(\"film_id\"," +
                                "\"genre_id\") " +
                                "VALUES (?, ?)",
                        keyHolder.getKey(),
                        genre.getId()
                        );
            }
        }

        film.setId(keyHolder.getKey().longValue());
        film.getMpa().setName(ratingDao.getRatingById(film.getMpa().getId()).getName());

        for (Genre genre : film.getGenres()) {
            genre.setName(genreDao.getGenreById(genre.getId()).getName());
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String query = "UPDATE \"films\" " +
                "SET \"name\" = ?," +
                "\"description\" = ?," +
                "\"release_date\" = ?," +
                "\"duration\" = ?," +
                "\"rating_id\" = ? " +
                "WHERE \"id\" = ?";

        jdbcTemplate.update(
                query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbcTemplate.update("DELETE FROM \"film_genre\" " +
                        "WHERE \"film_id\" = ?",
                film.getId()
        );

        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO \"film_genre\" " +
                                "(\"film_id\"," +
                                "\"genre_id\") " +
                                "VALUES (?, ?)",
                        film.getId(),
                        genre.getId()
                );
            }
        }

        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query(
                "SELECT * FROM \"films\"",
                (rs, rowNum) -> makeFilm(rs)
        );
    }

    @Override
    public Film getFilmById(Long id) {
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM \"films\" WHERE \"id\" = ?",
                    (rs, rowNum) -> makeFilm(rs),
                    id
            ).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void addUserLike(Long userId, Long filmId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
               "SELECT COUNT(*) \"count\" " +
                        "FROM \"film_likes\" " +
                        "WHERE \"film_id\" = ? and \"user_id\" = ?",
               filmId,
               userId
        );

        rs.next();
        if (rs.getLong("count") > 0)
            throw new ValidationException("Пользователь с ID "
                    + userId +
                    " уже поставил лайк фильму "
                    + filmId
            );

        jdbcTemplate.update(
                "INSERT INTO \"film_likes\" " +
                        "(\"film_id\", \"user_id\") " +
                        "VALUES (?, ?)",
                filmId,
                userId
        );
    }

    @Override
    public void removeUserLike(Long userId, Long filmId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"film_likes\" " +
                        "WHERE \"film_id\" = ? and \"user_id\" = ?",
                filmId,
                userId
        );

        rs.next();
        if (rs.getLong("count") == 0)
            throw new ValidationException("Пользователь с ID "
                    + userId +
                    " не ставил лайк фильму "
                    + filmId
            );

        jdbcTemplate.update(
                "DELETE FROM \"film_likes\" " +
                        "WHERE \"film_id\" = ? and \"user_id\" = ?",
                filmId,
                userId
        );
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());

        SqlRowSet ratingRs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"rating\" WHERE \"id\" = ?",
                rs.getLong("rating_id")
        );

        if (ratingRs.next()) {
            Rating rating = new Rating();
            rating.setId(ratingRs.getLong("id"));
            rating.setName(ratingRs.getString("name"));

            film.setMpa(rating);
        }

        SqlRowSet genreRs = jdbcTemplate.queryForRowSet(
                "SELECT \"g\".\"id\", \"g\".\"name\" " +
                        "FROM \"film_genre\" AS \"fg\" " +
                        "JOIN \"genre\" AS \"g\" ON \"g\".\"id\" = \"fg\".\"genre_id\" " +
                        "WHERE \"fg\".\"film_id\" = ?",
                film.getId()
        );

        while (genreRs.next()) {
            Genre genre = new Genre();
            genre.setId(genreRs.getLong("id"));
            genre.setName(genreRs.getString("name"));

            film.getGenres().add(genre);
        }


        SqlRowSet likesRs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"film_likes\" " +
                        "WHERE \"film_id\" = ?",
                film.getId()
        );

        while (likesRs.next()) {
            film.getUsersLikes().add(likesRs.getLong("user_id"));
        }

        return film;
    }
}
