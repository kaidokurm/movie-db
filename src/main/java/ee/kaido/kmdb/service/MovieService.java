package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final GenreService genreService;

    public MovieService(MovieRepository movieRepository, ActorRepository actorRepository, GenreRepository genreRepository, GenreService genreService) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.genreRepository = genreRepository;
        this.genreService = genreService;
    }

    public Movie addMovie(@Valid Movie movie) {

        validateMovieTitle(movie);
        validateMovieActorsAndGenres(movie);
        return movieRepository.save(movie);
    }

    private void validateMovieTitle(Movie movie) {
        if (movie.getTitle().equals(null) || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
    }

    private void validateMovieActorsAndGenres(Movie movie) {
        //find all actors by id
        List<Actor> actors = actorRepository.findAllById(movie.getActors().stream().map(Actor::getId).collect(Collectors.toList()));
        movie.setActors(actors);
        //find all genres by id
        List<Genre> genres = genreRepository.findAllById(movie.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
        movie.setGenres(genres);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie findMovieById(Long id) throws ResourceNotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    }

    public Movie updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Movie movie = findMovieById(id);

        if (updates.containsKey("title")) {
            movie.setTitle((String) updates.get("title"));
        }

        if (updates.containsKey("releasedYear")) {
            movie.setReleasedYear((Integer) updates.get("releasedYear"));
        }

        if (updates.containsKey("duration")) {
            movie.setDuration(Duration.parse((String) updates.get("duration")));
        }

        if (updates.containsKey("genres")) {
            List<Long> genresIds = ((List<Map<String, Object>>) updates.get("genres")).stream()
                    .map(genre -> (Long) genre.get("id"))
                    .toList();
            List<Genre> genres = new ArrayList<>();
            for (Long genreId : genresIds) {
                genres.add(genreRepository.findById(genreId).orElseThrow(() -> new ResourceNotFoundException("No genre found with id: " + genreId)));
            }
            movie.setGenres(genres);
        }

        if (updates.containsKey("actors")) {
            List<Long> actorIds = ((List<Map<String, Object>>) updates.get("actors")).stream()
                    .map(actor -> (Long) actor.get("id"))
                    .toList();
            List<Actor> actors = new ArrayList<>();
            for (Long actorId : actorIds) {
                actors.add(actorRepository.findById(actorId).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + actorId)));
            }
            movie.setActors(actors);
        }

        return movieRepository.save(movie);
    }

    public List<Movie> deleteMovie(Long id) {
        movieRepository.deleteById(id);
        return getAllMovies();
    }

    public List<Movie> getMoviesByFilter(Long genreId, Integer releaseYear, Long actorId) throws ResourceNotFoundException {
        List<Movie> movies = getAllMovies();

        movies = filterByGenre(genreId, movies);
        movies = filterByReleaseYear(releaseYear, movies);
        movies = filterByActor(actorId, movies);

        return movies;
    }

    private List<Movie> filterByGenre(Long genreId, List<Movie> movies) throws ResourceNotFoundException {
        if (genreId != null) {
            Genre genre = genreService.getGenreById(genreId);
            return movieRepository.findMoviesByGenreId(genre.getId());
        }
        return movies;
    }

    private List<Movie> filterByReleaseYear(Integer releaseYear, List<Movie> movies) {
        if (releaseYear != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getReleasedYear() == releaseYear)
                    .collect(Collectors.toList());
        }
        return movies;
    }

    private List<Movie> filterByActor(Long actorId, List<Movie> movies) {
        if (actorId != null) {
            movies = movies.stream()
                    .filter(movie -> movie.getActors().stream()
                            .anyMatch(actor -> actor.getId().equals(actorId)))
                    .collect(Collectors.toList());
        }
        return movies;
    }
}
