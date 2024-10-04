package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.*;
import ee.kaido.kmdb.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.service.checkers.Checks.checkIfStringNotEmpty;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ObjectMapper mapper;
    private final GenreService genreService;
    private final ActorService actorService;

    public MovieService(MovieRepository movieRepository, ObjectMapper mapper, GenreService genreService, ActorService actorService) {
        this.movieRepository = movieRepository;
        this.mapper = mapper;
        this.genreService = genreService;
        this.actorService = actorService;
    }

    public Movie addMovie(@Valid Movie movie) {
        validateMovieTitle(movie);
        validateMovieActorsAndGenres(movie);
        return movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) throws ResourceNotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
    }

    public List<MovieDTO> getMoviesByFilter(Long genreId, Integer releaseYear, Long actorId, String title) throws ResourceNotFoundException {
        Genre genre = null;
        Actor actor = null;
        if (genreId != null)
            genre = genreService.getGenreById(genreId);
        if (actorId != null)
            actor = actorService.getActorById(actorId);
        return movieRepository.getMoviesByFilters(genre, releaseYear, actor, title).stream()
                .map(movie -> new MovieDTO(movie, actorId == null))
                .collect(Collectors.toList());
    }

    public Movie updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Movie movie = getMovieById(id);
        JsonNode jsonNode = mapper.convertValue(updates, JsonNode.class);
        updateMovieFields(jsonNode, movie);
        return movieRepository.save(movie);
    }

    private void updateMovieFields(JsonNode jsonNode, Movie movie) throws ResourceNotFoundException {

        if (jsonNode.has("title")) {
            String title = jsonNode.get("title").asText();
            checkIfStringNotEmpty(title, "Movie title");
            movie.setTitle(title);
        }

        if (jsonNode.has("releasedYear")) {
            movie.setReleasedYear(jsonNode.get("releasedYear").asInt());
        }

        if (jsonNode.has("duration")) {
            String durationString = jsonNode.get("duration").asText();
            Duration duration = Duration.parse(durationString);
            movie.setDuration(duration);
        }

        if (jsonNode.has("genres")) {
            JsonNode genresJsonNode = jsonNode.get("genres");
            Set<Genre> genres = new HashSet<>();
            genresJsonNode.forEach(genre -> {
                try {
                    genres.add(genreService.getGenreById(genre.get("id").asLong()));
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            movie.setGenres(genres);
        }

        if (jsonNode.has("actors")) {
            List<Actor> actors = new ArrayList<>();
            JsonNode actorsJsonNode = jsonNode.get("actors");
            for (JsonNode actorJsonNode : actorsJsonNode) {
                long actorId = actorJsonNode.get("id").asLong();
                actors.add(actorService.getActorById(actorId));
            }
            movie.setActors(actors);
        }
    }

    public String deleteMovie(Long id) {
        movieRepository.deleteById(id);
        return "Movie with id: " + id + " was deleted";
    }

    private void validateMovieActorsAndGenres(Movie movie) {
        //find all actors by id
        if (!movie.getActors().isEmpty()) {
            List<Actor> actors = movie.getActors().stream().map(actor -> {
                        try {
                            return actorService.getActorById(actor.getId());
                        } catch (ResourceNotFoundException e) {
                            throw new RuntimeException("One or more Actor were not found", e);
                        }
                    }
            ).collect(Collectors.toList());
            movie.setActors(actors);
        }

        //find all genres by id
        if (movie.getGenres() != null) {
            Set<Genre> genres = movie.getGenres().stream().map(genre -> {
                try {
                    return genreService.getGenreById(genre.getId());
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
            movie.setGenres(genres);
        }
    }

    private void validateMovieTitle(Movie movie) {
        if (movie.getTitle().equals(null) || movie.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
    }


    public List<ActorDTO> getActorsInMovie(Long movieId) throws ResourceNotFoundException {
        try {
            return getMovieById(movieId).getActors().stream().map(actor -> new ActorDTO(actor, false)).collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("No actors found");
        }
    }
}
