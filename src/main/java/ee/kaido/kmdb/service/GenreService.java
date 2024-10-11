package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
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
    @Autowired
    private MovieRepository movieRepository;

    public Genre addGenre(Genre genre) throws ElementExistsException {
        validateGenre(genre);
        return genreRepository.save(genre);
    }

    public List<Genre> getAllGenresByName(String name) {
        return genreRepository.findAllByName(name);
    }

    public Genre getGenreByIdOrThrowError(Long id) throws ResourceNotFoundException {
        return genreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No genre found with id: " + id));
    }

    public Genre updateGenre(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Genre genre = getGenreByIdOrThrowError(id);
        updateGenreName(updates, genre);
        return genreRepository.save(genre);
    }

    public String deleteGenre(Long id, boolean force) throws BadRequestException, ResourceNotFoundException {
        //try to get genre
        Genre genre = getGenreByIdOrThrowError(id);
        int movieCount = movieRepository.getMoviesByFilters(genre, null, null, null).size();
        if (!force)
            if (movieCount != 0)
                throw new BadRequestException("Cannot delete genre '"
                        + genre.getName()
                        + "' because it has "
                        + movieCount
                        + " associated movie"
                        + (movieCount > 1 ? "s" : ""));//if more then 1 add s
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
