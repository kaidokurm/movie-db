package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.service.checkers.Checks.checkIfStringNotEmpty;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final ObjectMapper mapper;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, ObjectMapper mapper, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.mapper = mapper;
        this.movieRepository = movieRepository;
    }

    public ActorDTO addActor(Map<String, Object> data) throws ElementExistsException, BadRequestException, ResourceNotFoundException {
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        if (jsonNode.has("id"))
            checkIfActorIdExists(jsonNode.get("id").asLong());
        Actor actor = new Actor();
        updateActorFields(jsonNode, actor);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, true);
    }

    public List<ActorDTO> getActorsByFilter(String name, boolean showMovies) {
        return actorRepository.findByNameContains(name)
                .stream()
                .map(actor -> new ActorDTO(actor, showMovies))
                .collect(Collectors.toList());
    }

    public ActorDTO findActorDtoById(long id) throws ResourceNotFoundException {
        return new ActorDTO(actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id)), true);
    }

    public Actor getActorById(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public ActorDTO updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        Actor actor = actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor, true);
    }

    public String deleteActor(Long id) throws ResourceNotFoundException {
        getActorById(id);
        actorRepository.deleteById(id);
        return "Actor with id " + id + " was deleted";
    }

    private void checkIfActorIdExists(Long id) throws ElementExistsException {
        if (id != null && actorRepository.findById(id).isPresent())
            throw new ElementExistsException("Actor with id " + id + " already exists");
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws BadRequestException, ResourceNotFoundException {
        updateName(jsonNode, actor);
        updateBirthDate(jsonNode, actor);
        updateMovies(jsonNode, actor);
    }

    private void updateMovies(JsonNode jsonNode, Actor actor) throws ResourceNotFoundException {
        if (jsonNode.has("movies")) {
            List<Movie> movies = new ArrayList<>();
            JsonNode moviesJsonNode = jsonNode.get("movies");
            removeActorFromMovie(actor);
            for (JsonNode movieJsonNode : moviesJsonNode) {
                Long movieId = movieJsonNode.get("id").asLong();
                Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId));
                movies.add(movie);
                movie.getActors().add(actor);
                movieRepository.save(movie);
            }
            actor.setMovies(movies);
        }
    }

    private void removeActorFromMovie(Actor actor) {
        List<Movie> movies = movieRepository.getMoviesByFilters(null, null, actor, null);
        movies.forEach((movie) -> movie.removeActor(actor));
    }

    private static void updateBirthDate(JsonNode jsonNode, Actor actor) throws BadRequestException {
        if (jsonNode.has("birthDate")) {
            actor.setBirthDate(jsonNode.get("birthDate").asText());
        }
    }

    private static void updateName(JsonNode jsonNode, Actor actor) {
        if (jsonNode.has("name")) {
            actor.setName(
                    checkIfStringNotEmpty(jsonNode.get("name").asText(), "Actor name")
            );
        }
    }
}