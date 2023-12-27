package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.RatingDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDao;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.RatingDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.*;

@JdbcTest
@Sql({"/schema.sql", "/data.sql"})
public class FilmDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film film1;
    private Film film2;
    private User user1;

    @BeforeEach
    public void setUp() {
        Rating rating = new Rating();
        Genre genre = new Genre();
        rating.setId(1L);
        genre.setId(1L);

        film1 = new Film();
        film1.setName("testfilm1");
        film1.setDescription("test1 description");
        film1.setReleaseDate(LocalDate.of(1967, 1, 1));
        film1.setDuration(50);
        film1.setMpa(rating);
        film1.setGenres(Set.of(genre));

        rating.setId(2L);
        genre.setId(2L);

        film2 = new Film();
        film2.setName("testfilm2");
        film2.setDescription("test2 description");
        film2.setReleaseDate(LocalDate.of(2011, 6, 23));
        film2.setDuration(120);
        film2.setMpa(rating);
        film2.setGenres(Set.of(genre));

        user1 = new User();
        user1.setEmail("test1@yandex.ru");
        user1.setLogin("testlogin1");
        user1.setName("testname1");
        user1.setBirthday(LocalDate.of(1995, 12, 29));
    }

    @Test
    public void createFilm() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);

        Film createdFilm = filmDao.createFilm(film1);

        assertEquals(createdFilm.getName(), film1.getName());
        assertEquals(createdFilm.getDescription(), film1.getDescription());
        assertEquals(createdFilm.getDuration(), film1.getDuration());
        assertEquals(createdFilm.getReleaseDate(), film1.getReleaseDate());
    }

    @Test
    public void updateFilm() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);

        Long id = filmDao.createFilm(film1).getId();

        film2.setId(id);
        filmDao.updateFilm(film2);
        Film newFilm = filmDao.getFilmById(id);

        assertEquals(newFilm.getName(), film2.getName());
        assertEquals(newFilm.getDescription(), film2.getDescription());
        assertEquals(newFilm.getDuration(), film2.getDuration());
        assertEquals(newFilm.getReleaseDate(), film2.getReleaseDate());
    }

    @Test
    public void getFilmById() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);

        Long id = filmDao.createFilm(film1).getId();
        Film getFilm = filmDao.getFilmById(id);

        assertEquals(getFilm.getName(), film1.getName());
        assertEquals(getFilm.getDescription(), film1.getDescription());
        assertEquals(getFilm.getDuration(), film1.getDuration());
        assertEquals(getFilm.getReleaseDate(), film1.getReleaseDate());
    }

    @Test
    public void getFilms() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);

        filmDao.createFilm(film1);
        filmDao.createFilm(film2);

        assertEquals(2, filmDao.getFilms().size());
    }

    @Test
    public void addUserLike() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);
        UserDao userDao = new UserDao(jdbcTemplate);

        Long filmId = filmDao.createFilm(film1).getId();
        Long userId = userDao.createUser(user1).getId();

        filmDao.addUserLike(userId, filmId);

        film1 = filmDao.getFilmById(filmId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"film_likes\" "
        );

        rs.next();

        Assertions.assertFalse(film1.getUsersLikes().isEmpty());
        assertEquals(1, rs.getLong("count"));
    }

    @Test
    public void removeUserLike() {
        RatingDao ratingDao = new RatingDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        FilmDao filmDao = new FilmDao(jdbcTemplate, genreDao, ratingDao);
        UserDao userDao = new UserDao(jdbcTemplate);

        Long filmId = filmDao.createFilm(film1).getId();
        Long userId = userDao.createUser(user1).getId();

        filmDao.addUserLike(userId, filmId);
        filmDao.removeUserLike(userId, filmId);

        film1 = filmDao.getFilmById(filmId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"film_likes\" "
        );

        rs.next();

        assertTrue(film1.getUsersLikes().isEmpty());
        assertEquals(0, rs.getLong("count"));
    }
}
