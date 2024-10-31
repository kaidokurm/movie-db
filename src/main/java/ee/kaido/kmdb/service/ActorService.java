package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.deserializers.checkers.Checks.checkIfStringNotEmpty;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;
    private final ObjectMapper mapper;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository, ObjectMapper mapper) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.mapper = mapper;
    }

    public ActorDTO createActor(ActorDTO actorDTO) throws BadRequestException {
        Actor actor = new Actor(actorDTO);
        List<Movie> movies = new ArrayList<>();
        setActorMovies(actorDTO, actor, movies);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, false);
    }

    private void setActorMovies(ActorDTO actorDTO, Actor actor, List<Movie> movies) {
        if (actorDTO.getMovies() != null) {
            List<Long> movieIds = actorDTO.getMovies().stream().map(MovieDTO::getId).toList();
            movieIds.forEach(movieId -> {
                try {
                    Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
                    movie.getActors().add(actor);
                    movies.add(movie);
                } catch (ResourceNotFoundException e) {
                    throw new RuntimeException(e);
                }
                actor.setMovies(movies);
            });
        }
    }

    public List<ActorDTO> getActorsByFilter(String name, Integer page, Integer size, Boolean hideMovies) throws BadRequestException {
        //check if Pageable is ok
        if (page != null && size != null) {
            if (page >= 0 && size > 0 && size <= 200) {
                Pageable pageable = PageRequest.of(page, size);
                return actorRepository
                        .findByNameContainsPageable(name, pageable)
                        .stream()
                        .map(actor -> new ActorDTO(actor, hideMovies))
                        .collect(Collectors.toList());
            } else {
                throw new BadRequestException("Page must be at least 0 and size must be between 0 and 200");
            }
        } else {
            //else get list without pageable
            return actorRepository
                    .findByNameContains(name)
                    .stream()
                    .map(actor -> new ActorDTO(actor, hideMovies))
                    .collect(Collectors.toList());
        }
    }

    public ActorDTO findActorDtoById(long id, boolean hideMovies) throws ResourceNotFoundException {
        return new ActorDTO(getActorByIdOrThrowError(id), hideMovies);
    }

    public Actor getActorByIdOrThrowError(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public ActorDTO updateActor(Long id, Map<String, Object> data, boolean hideMovies) throws ResourceNotFoundException, BadRequestException {
        Actor actor = getActorByIdOrThrowError(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, hideMovies);
    }

    public void deleteActor(Long id, boolean force) throws ResourceNotFoundException, BadRequestException {
        Actor actor = getActorByIdOrThrowError(id);
        if (!force) {
            int movieCount = movieRepository.getMoviesByFilters(null, null, actor, null).size();
            if (movieCount != 0)
                throw new BadRequestException("Unable to delete actor '" + actor.getName() +
                        "' as they are associated with " + movieCount + " movie" +
                        (movieCount > 1 ? "s" : ""));
        }
        removeActorFromMovies(actor);
        actorRepository.deleteById(id);
    }

    private void removeActorFromMovies(Actor actor) {
        List<Movie> movies = movieRepository.getMoviesByFilters(null, null, actor, null);
        movies.forEach((movie) -> movie.removeActor(actor));
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws BadRequestException, ResourceNotFoundException {
        actor.setName(updateName(jsonNode));
        actor.setBirthDate(updateBirthDate(jsonNode));
        actor.setMovies(updateMovies(jsonNode, actor));
    }

    private List<Movie> updateMovies(JsonNode jsonNode, Actor actor) throws ResourceNotFoundException {
        List<Movie> movies = new ArrayList<>();
        if (jsonNode.has("movies")) {
            JsonNode moviesJsonNode = jsonNode.get("movies");

            removeActorFromMovies(actor);
            for (JsonNode movieJsonNode : moviesJsonNode) {
                long movieId = movieJsonNode.has("id") ? movieJsonNode.get("id").asLong() : movieJsonNode.asLong();
                Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
                movies.add(movie);
                movie.getActors().add(actor);
                movieRepository.save(movie);
            }
        }
        return movies;
    }

    private static String updateBirthDate(JsonNode jsonNode) {
        String birthdate = "";
        if (jsonNode.has("birthDate")) {
            birthdate = jsonNode.get("birthDate").asText();
        }
        return birthdate;
    }

    private static String updateName(JsonNode jsonNode) throws BadRequestException {
        if (jsonNode.has("name")) {
            return checkIfStringNotEmpty(jsonNode.get("name").asText(), "Actor name");
        } else {
            throw new BadRequestException("Actor name is required.");
        }
    }

    public List<MovieDTO> getActorMovies(long id) throws ResourceNotFoundException {

        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id))
                .getMovies()
                .stream()
                .map(movie ->
                        new MovieDTO(movie, false))
                .collect(Collectors.toList());
    }
}