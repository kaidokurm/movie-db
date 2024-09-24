package ee.kaido.kmdb.service;

import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Genre addGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found"));
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
