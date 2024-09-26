package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Genre addGenre(Genre genre) throws ElementExistsException {
        try {
            if (genre.getId() != null && genreRepository.findById(genre.getId()).isPresent()) {
                throw new ElementExistsException("Genre with id " + genre.getId() + " already exists");
            }
            return genreRepository.save(genre);
        } catch (DataIntegrityViolationException e) {
            throw new ElementExistsException("Genre already exists");
        }
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No genre found with id: " + id));
    }

    public Genre updateGenre(Long id, Map<String, Object> updates) {
        Genre genre = getGenreById(id);
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Genre.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, genre, value);
        });
        return genreRepository.save(genre);
    }

    public List<Genre> deleteGenre(Long id) {
        genreRepository.deleteById(id);
        return getAllGenres();
    }
}
