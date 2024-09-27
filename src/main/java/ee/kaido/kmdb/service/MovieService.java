package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie addMovie(@Valid Movie movie) {
        System.out.println("How");
        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            System.out.println("Title is empty");
            throw new IllegalArgumentException("Movie must have a title");
        }
        System.out.println("Far");
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) throws ResourceNotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    }

    public Movie updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Movie movie = getMovieById(id);
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Movie.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, movie, value);
        });
        return movieRepository.save(movie);
    }

    public List<Movie> deleteMovie(Long id) {
        movieRepository.deleteById(id);
        return getAllMovies();
    }

    public List<Movie> getMoviesByFilter(Long genreId, Integer releaseYear, Long actorId) {
        List<Movie> movies = getAllMovies();

        if (genreId != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getGenre().getId().equals(genreId))
                    .collect(Collectors.toList());
        }

        if (releaseYear != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getReleasedYear() == releaseYear)
                    .collect(Collectors.toList());
        }

        if (actorId != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getActor().stream()
                            .anyMatch(actor -> actor.getId().equals(actorId)))
                    .collect(Collectors.toList());
        }

        return movies;
    }
}
