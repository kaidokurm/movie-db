package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.annotation.Validated;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Validated
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Genre addGenre(Genre genre) throws ElementExistsException {
        if (genre.getId() != null && genreRepository.findById(genre.getId()).isPresent())
            throw new ElementExistsException("Genre with id " + genre.getId() + " already exists");
        if (genre.getName().trim().isEmpty())
            throw new IllegalArgumentException("Genre name can't be empty!");
        if (genreRepository.findByName(genre.getName()) != null)
            throw new ElementExistsException("Genre with name " + genre.getName() + " already exists");
        return genreRepository.save(genre);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre getGenreById(Long id) throws ResourceNotFoundException {
        return genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No genre found with id: " + id));
    }

    public Genre updateGenre(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Genre genre = getGenreById(id);
        updates.forEach((key, value) -> {
            if (Objects.equals(key, "name") && (value == null || value == ""))
                throw new IllegalArgumentException("Name cannot be empty");
            Field field = ReflectionUtils.findField(Genre.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, genre, value);
        });
        return genreRepository.save(genre);
    }

    public List<Genre> deleteGenre(Long id) throws ResourceNotFoundException {
        this.getGenreById(id);
        genreRepository.deleteById(id);
        return getAllGenres();
    }
}
