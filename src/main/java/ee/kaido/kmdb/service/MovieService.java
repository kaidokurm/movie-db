package ee.kaido.kmdb.service;

import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    public List<Movie> getMovieByGenre(String genre) {
        return movieRepository.findByGenreName(genre);
    }

    public Movie updateMovie(Long id, Map<String, Object> updates) {
        Movie movie = getMovieById(id);
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Movie.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, movie, value);
        });
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
