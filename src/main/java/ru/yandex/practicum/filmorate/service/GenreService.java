package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre getGenreById(Long genreId) {
        if (genreDao.getGenreById(genreId) == null)
            throw new NotFoundException("Жанра с ID " + genreId + " не существует");
        return genreDao.getGenreById(genreId);
    }

    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }
}
