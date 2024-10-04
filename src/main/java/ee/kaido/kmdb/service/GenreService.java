package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Service
@Validated
public class GenreService {
    @Autowired
    private GenreRepository genreRepository;

    public Genre addGenre(Genre genre) throws ElementExistsException {
        validateGenre(genre);
        return genreRepository.save(genre);
    }

    public List<Genre> getAllGenresByName(String name) {
        return genreRepository.findAllByName(name);
    }

    public Genre getGenreById(Long id) throws ResourceNotFoundException {
        return genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No genre found with id: " + id));
    }

    public Genre updateGenre(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Genre genre = getGenreById(id);
        updateGenreName(updates, genre);
        return genreRepository.save(genre);
    }

    public String deleteGenre(Long id) throws ResourceNotFoundException {
        this.getGenreById(id);
        genreRepository.deleteById(id);
        return "Genre with id " + id + " has been deleted";
    }

    private static void updateGenreName(Map<String, Object> updates, Genre genre) {
        if (updates.containsKey("name")) {
            String newName = (String) updates.get("name");
            if (newName == null || newName.trim().isEmpty())
                throw new IllegalArgumentException("Genre name can't be empty!");
            genre.setName(newName);
        }
    }

    private void validateGenre(Genre genre) throws ElementExistsException {
        if (genre.getId() != null && genreRepository.findById(genre.getId()).isPresent())
            throw new ElementExistsException("Genre with id " + genre.getId() + " already exists");
        if (genre.getName() == null || genre.getName().trim().isEmpty())
            throw new IllegalArgumentException("Genre name is required");
        if (genreRepository.findByName(genre.getName()) != null)
            throw new ElementExistsException("Genre with name " + genre.getName() + " already exists");
    }
}
