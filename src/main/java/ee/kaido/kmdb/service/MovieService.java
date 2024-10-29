package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.deserializers.DurationDeserializer;
import ee.kaido.kmdb.model.*;
import ee.kaido.kmdb.repository.MovieRepository;
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
        assert objectMapper != null;
        objectMapper.registerModule(module);
    }

    public MovieDTO addMovie(MovieDTO movieDto) {
        Movie movie = new Movie(movieDto);

        updateMovieActors(movieDto, movie);

        // Save and return movie with id
        Movie savedMovie = movieRepository.save(movie);
        return new MovieDTO(savedMovie, true);
    }

    private void updateMovieActors(MovieDTO movieDto, Movie movie) {
        //update actors when there is actors in input
        if (movieDto.getActors() != null && !movieDto.getActors().isEmpty()) {
            List<Actor> actors = movieDto.getActors().stream().map(actorDto -> {
                long actorId = actorDto.getId();
                try {
                    return actorService.getActorByIdOrThrowError(actorId);
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            movie.setActors(actors);
        }
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
        if (page != null && page >= 0 && size != null && size > 0) {
            return getFilteredMoviesPageable(releaseYear, title, page, size, genre, actor);
        } else {
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

    public MovieDTO updateMovie(Long id, Map<String, Object> updates) throws ResourceNotFoundException, BadRequestException {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + id));
        JsonNode jsonNode = objectMapper.convertValue(updates, JsonNode.class);
        updateMovieFields(jsonNode, movie);
        return new MovieDTO(movieRepository.save(movie), true);
    }

    private void updateMovieFields(JsonNode movieUpdateJson, Movie movie) throws ResourceNotFoundException, BadRequestException {
        updateMovieTitle(movieUpdateJson, movie);
        updateMovieReleasedYear(movieUpdateJson, movie);
        updateMovieDuration(movieUpdateJson, movie);
        updateMovieGenres(movieUpdateJson, movie);
        updateJsonMovieActors(movieUpdateJson, movie);
    }


    public void deleteMovie(Long id) throws ResourceNotFoundException {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null)
            throw new ResourceNotFoundException("No movie found with id: " + id);
        movieRepository.deleteById(id);
    }

    public List<ActorDTO> getActorsInMovie(Long movieId) throws ResourceNotFoundException {
        try {
            Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
            return movie.getActors().stream().map(actor -> new ActorDTO(actor, false)).collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("No actors found");
        }
    }

    private void updateJsonMovieActors(JsonNode jsonNode, Movie movie) throws ResourceNotFoundException {
        //update actors when there is actors in input
        if (jsonNode.has("actors")) {
            List<Actor> actorList = new ArrayList<>();
            JsonNode actors = jsonNode.get("actors");
            for (JsonNode actor : actors) {
                long actorId = actor.has("id") ? actor.get("id").asLong() : actor.asLong();
                actorList.add(actorService.getActorByIdOrThrowError(actorId));
            }
            movie.setActors(actorList);
        }
    }

    private void updateMovieGenres(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("genres")) {
            JsonNode genresJsonNode = jsonNode.get("genres");
            Set<Genre> genres = new HashSet<>();
            genresJsonNode.forEach(genre -> {
                Long genreId = genresJsonNode.has("id") ? genresJsonNode.get("id").asLong() : genre.asLong();
                try {
                    genres.add(genreService.getGenreByIdOrThrowError(genreId));
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

    private void updateMovieReleasedYear(JsonNode jsonNode, Movie movie) {
        if (jsonNode.has("releasedYear")) {
            movie.setReleasedYear(jsonNode.get("releasedYear").asInt());
        }
    }

    private void updateMovieTitle(JsonNode jsonNode, Movie movie) throws BadRequestException {
        if (jsonNode.has("title")) {
            String title = jsonNode.get("title").asText();
            checkIfStringNotEmpty(title, "Movie title");
            movie.setTitle(title);
        }
    }
}
