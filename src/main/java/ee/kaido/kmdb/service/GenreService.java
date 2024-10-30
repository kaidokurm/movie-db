package ee.kaido.kmdb.service;

import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ElementExistsException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Validated
public class GenreService {
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

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

    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    public Genre updateGenre(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Genre genre = getGenreByIdOrThrowError(id);
        updateGenreName(updates, genre);
        return genreRepository.save(genre);
    }

    public void deleteGenre(Long id, boolean force) throws BadRequestException, ResourceNotFoundException {
        //try to get genre
        Genre genre = getGenreByIdOrThrowError(id);
        if (!force) {
            int movieCount = movieRepository.getMoviesByFilters(genre, null, null, null).size();
            if (movieCount != 0)
                throw new BadRequestException("Cannot delete genre '"
                        + genre.getName()
                        + "' because it has "
                        + movieCount
                        + " associated movie"
                        + (movieCount > 1 ? "s" : ""));//if more then 1 add s
        }
//        removeGenreFromMovies(genre);
        genreRepository.deleteById(id);
    }

//    private void removeGenreFromMovies(Genre genre) {
//        List<Movie> movies = movieRepository.getMoviesByFilters(genre, null, null, null);
//        for (Movie movie : movies) {
//            movie.removeGenre(genre);
//        }
//    }

    private void updateGenreName(Map<String, Object> updates, Genre genre) {
        if (updates.containsKey("name")) {
            String newName = (String) updates.get("name");
            if (genreRepository.findByName(newName) != null) {
                throw new IllegalArgumentException("Genre with name: '" + newName + "' already exists");
            }
            if (newName == null || newName.trim().isEmpty())
                throw new IllegalArgumentException("Genre name can't be empty!");
            genre.setName(newName);
        }
    }

    private void validateGenre(Genre genre) throws ElementExistsException {
        if (genre.getId() != null)
            genre.setId(null);

        if (genre.getName().trim().isEmpty())
            throw new IllegalArgumentException("Genre name can not be only spaces");

        if (genreRepository.findByName(genre.getName()) != null)
            throw new ElementExistsException("Genre with name '" + genre.getName() + "' already exists");
    }
}
