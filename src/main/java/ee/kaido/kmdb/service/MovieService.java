package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.deserializers.DurationDeserializer;
import ee.kaido.kmdb.model.*;
import ee.kaido.kmdb.repository.MovieRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.service.checkers.Checks.checkIfStringNotEmpty;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final ActorService actorService;
    private final ObjectMapper objectMapper;

    public MovieService(MovieRepository movieRepository, GenreService genreService, ActorService actorService, ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.actorService = actorService;
        this.objectMapper = objectMapper;
        registerDurationDeserializer();
    }

    private void registerDurationDeserializer() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Duration.class, new DurationDeserializer());
        objectMapper.registerModule(module);
    }

    public MovieDTO addMovie(@Valid Movie movie) {
        validateMovieTitle(movie);
        validateMovieActors(movie);
        validateMovieGenres(movie);
        return new MovieDTO(movieRepository.save(movie), true);
    }

    public MovieDTO getMovieById(Long id) throws ResourceNotFoundException {
        return new MovieDTO(movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id)), true);
    }

    public List<MovieDTO> getMoviesByFilter(Long genreId,
                                            Integer releaseYear,
                                            Long actorId,
                                            String title,
                                            Integer page,
                                            Integer size) throws ResourceNotFoundException {
        Genre genre = null;
        Actor actor = null;
        if (genreId != null)
            genre = genreService.getGenreByIdOrThrowError(genreId);
        if (actorId != null)
            actor = actorService.getActorByIdOrThrowError(actorId);

        //check if Pageable is ok
        if(page!=null&&page>=0&&size!=null&&size>0) {
            return getFilteredMoviesPageable(releaseYear, title, page, size, genre, actor);
        }else{
            //else get list without pageable
            return getMovieDTOS(releaseYear, title, genre, actor);
        }
    }

    private List<MovieDTO> getMovieDTOS(Integer releaseYear, String title, Genre genre, Actor actor) {
        return movieRepository
                .getMoviesByFilters(
                        genre,
                        releaseYear,
                        actor,
                        title)
                .stream()
                .map(movie -> new MovieDTO(movie, true))
                .collect(Collectors.toList());
    }

    private List<MovieDTO> getFilteredMoviesPageable(Integer releaseYear, String title, Integer page, Integer size, Genre genre, Actor actor) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository
                .getMoviesByFiltersPageable(
                        genre,
                        releaseYear,
                        actor,
                        title,
                        pageable)
                .stream()
                .map(movie -> new MovieDTO(movie, true))
                .collect(Collectors.toList());
    }

    public MovieDTO updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
        JsonNode jsonNode = objectMapper.convertValue(updates, JsonNode.class);
        updateMovieFields(jsonNode, movie);
        return new MovieDTO(movieRepository.save(movie), true);
    }

    private void updateMovieFields(JsonNode jsonNode, Movie movie) throws ResourceNotFoundException {

        updateMovieTitle(jsonNode, movie);
        updateMovieReleasedYear(jsonNode, movie);
        updateMovieDuration(jsonNode, movie);
        updateMovieGenres(jsonNode, movie);
        updateMovieActors(jsonNode, movie);
    }

    private void updateMovieActors(JsonNode jsonNode, Movie movie) throws ResourceNotFoundException {
        if (jsonNode.has("actors")) {
            List<Actor> actors = new ArrayList<>();
            JsonNode actorsJsonNode = jsonNode.get("actors");
            for (JsonNode actorJsonNode : actorsJsonNode) {
                long actorId = actorJsonNode.get("id").asLong();
                actors.add(actorService.getActorByIdOrThrowError(actorId));
            }
            movie.setActors(actors);
        }
    }

    private void updateMovieGenres(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("genres")) {
            JsonNode genresJsonNode = jsonNode.get("genres");
            Set<Genre> genres = new HashSet<>();
            genresJsonNode.forEach(genre -> {
                try {
                    genres.add(genreService.getGenreByIdOrThrowError(genre.get("id").asLong()));
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            movie.setGenres(genres);
        }
    }

    private void updateMovieDuration(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("duration")) {
            String durationString = jsonNode.get("duration").asText();
            Duration duration = objectMapper.convertValue(durationString, Duration.class);
            movie.setDuration(duration);
        }
    }

    private static void updateMovieReleasedYear(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("releasedYear")) {
            movie.setReleasedYear(jsonNode.get("releasedYear").asInt());
        }
    }

    private static void updateMovieTitle(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("title")) {
            String title = jsonNode.get("title").asText();
            checkIfStringNotEmpty(title, "Movie title");
            movie.setTitle(title);
        }
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }


    private void validateMovieActors(Movie movie) {
        List<Actor> actors=movie.getActors();
        //find all actors by id
        if (actors!=null&&!actors.isEmpty()) {
            List<Actor> newActors = movie.getActors().stream().map(actor -> {
                        try {
                            return actorService.getActorByIdOrThrowError(actor.getId());
                        } catch (ResourceNotFoundException e) {
                            throw new RuntimeException("One or more Actor were not found", e);
                        }
                    }
            ).collect(Collectors.toList());
            movie.setActors(newActors);
        }
    }

    private void validateMovieGenres(Movie movie) {
        //find all genres by id
        if (movie.getGenres() != null) {
            Set<Genre> genres = movie.getGenres().stream().map(genre -> {
                try {
                    return genreService.getGenreByIdOrThrowError(genre.getId());
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
            movie.setGenres(genres);
        }
    }

    private void validateMovieTitle(Movie movie) {
        String title = movie.getTitle();
        if (title==null|| title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title cannot be empty");
        }
    }


    public List<ActorDTO> getActorsInMovie(Long movieId) throws ResourceNotFoundException {
        try {
            Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
            return movie.getActors().stream().map(actor -> new ActorDTO(actor, false)).collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("No actors found");
        }
    }
}
