package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, ActorRepository actorRepository, GenreRepository genreRepository) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.genreRepository = genreRepository;
    }

    public Movie addMovie(@Valid Movie movie) {
        //without optional cant trim if title=null
        if (!Optional.ofNullable(movie.getTitle()).map(String::trim).filter(s -> !s.isEmpty()).isPresent()) {
            throw new IllegalArgumentException("Movie must have a title");
        }
        //find all actors bi id
        List<Actor> actors = actorRepository.findAllById(movie.getActor().stream().map(Actor::getId).collect(Collectors.toList()));
        movie.setActor(actors);
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) throws ResourceNotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    }

    public Movie updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException, IOException {
        Movie movie = getMovieById(id);

        if (updates.containsKey("title")) {
            movie.setTitle((String) updates.get("title"));
        }

        if (updates.containsKey("releasedYear")) {
            movie.setReleasedYear((Integer) updates.get("releasedYear"));
        }

        if (updates.containsKey("duration")) {
            movie.setDuration(Duration.parse((String) updates.get("duration")));
        }

        if (updates.containsKey("genre")) {
            Long genreId = (Long) updates.get("genre");
            movie.setGenre(genreRepository.findById(genreId).orElseThrow(() -> new ResourceNotFoundException("No genre found with id: " + genreId)));
        }

        if (updates.containsKey("actor")) {
            
            List<Long> actorId = ((List<Map<String, Object>>) updates.get("actor")).stream()
                    .map(actor -> (Long) actor.get("id"))
                    .collect(Collectors.toList());
            movie.setActor(actorRepository.findAllById(actorId));
        }

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
