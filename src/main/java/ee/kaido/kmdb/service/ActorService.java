package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.model.MovieDTO;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.service.checkers.Checks.checkIfActorIdExists;
import static ee.kaido.kmdb.service.checkers.Checks.checkIfStringNotEmpty;

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

    public ActorDTO createActor(Map<String, Object> data) throws ElementExistsException, BadRequestException, ResourceNotFoundException {
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        if (jsonNode.has("id"))
            checkIfActorIdExists(jsonNode.get("id").asLong(), actorRepository);
        Actor actor = new Actor();
        updateActorFields(jsonNode, actor, true);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, true);
    }

    public List<ActorDTO> getActorsByFilter(String name) {
        return actorRepository.findByNameContains(name)
                .stream()
                .map(actor -> new ActorDTO(actor, true))
                .collect(Collectors.toList());
    }

    public ActorDTO findActorDtoById(long id) throws ResourceNotFoundException {
        return new ActorDTO(getActorByIdOrThrowError(id), true);
    }

    public Actor getActorByIdOrThrowError(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public ActorDTO updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        Actor actor = getActorByIdOrThrowError(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor, false);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, true);
    }

    public void deleteActor(Long id, boolean force) throws ResourceNotFoundException, BadRequestException {
        Actor actor = getActorByIdOrThrowError(id);
        if (!force) {
            int movieCount = actor.getMovies().size();
            if (movieCount != 0)
                throw new BadRequestException("Unable to delete actor '" + actor.getName() +
                        "' as they are associated with " + movieCount + " movie" +
                        (movieCount > 1 ? "s" : ""));
        }
        actorRepository.deleteById(id);
    }

    private void removeActorFromMovie(Actor actor) {
        List<Movie> movies = movieRepository.getMoviesByFilters(null, null, actor, null);
        movies.forEach((movie) -> movie.removeActor(actor));
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor, boolean create) throws BadRequestException, ResourceNotFoundException {
        actor.setName(updateName(jsonNode));
        actor.setBirthDate(updateBirthDate(jsonNode));
        actor.setMovies(updateMovies(jsonNode, actor, create));
    }

    private List<Movie> updateMovies(JsonNode jsonNode, Actor actor, boolean create) throws ResourceNotFoundException {
        List<Movie> movies = new ArrayList<>();
        if (jsonNode.has("movies")) {
            JsonNode moviesJsonNode = jsonNode.get("movies");
            if (!create && actor != null) {
                removeActorFromMovie(actor);
            }
            for (JsonNode movieJsonNode : moviesJsonNode) {
                long movieId;
                if (movieJsonNode.has("id")) {
                    movieId = movieJsonNode.get("id").asLong();
                } else {//if its a list of longs
                    movieId = movieJsonNode.asLong();
                }
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

    public List<MovieDTO> getActorMovies(long id) {

        return actorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No actor found with id: " + id))
                .getMovies()
                .stream()
                .map(movie ->
                        new MovieDTO(movie, false))
                .collect(Collectors.toList());
    }
}