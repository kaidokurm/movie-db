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

    public List<ActorDTO> getActorsByFilter(String name) {
        return actorRepository.findByNameContains(name).stream().map(actor -> new ActorDTO(actor, true)).collect(Collectors.toList());
    }

    public ActorDTO findActorDtoById(long id) throws ResourceNotFoundException {
        return new ActorDTO(actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id)), true);
    }

    public Actor getActorById(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public ActorDTO updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        Actor actor = actorRepository.getOne()getActorById(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        Actor savedActor = actorRepository.updateActor(actor).save(actor);
        return new ActorDTO(savedActor, true);
    }

    public String deleteActor(Long id) throws ResourceNotFoundException {
        this.getActorById(id);
        actorRepository.deleteById(id);
        return "Actor with id " + id + " was deleted";
    }

    private void checkIfActorIdExists(Long id) throws ElementExistsException {
        if (id != null && actorRepository.findById(id).isPresent())
            throw new ElementExistsException("Actor with id " + id + " already exists");
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws BadRequestException, ResourceNotFoundException {
        if (jsonNode.has("name")) {
            actor.setName(
                    checkIfStringNotEmpty(jsonNode.get("name").asText(), "Actor name")
            );
        }
        if (jsonNode.has("birthDate")) {
            actor.setBirthDate(jsonNode.get("birthDate").asText());
        }
        if (jsonNode.has("movies")) {
            List<Movie> movies = new ArrayList<>();
            JsonNode moviesJsonNode = jsonNode.get("movies");
            for (JsonNode movieJsonNode : moviesJsonNode) {
                Long movieId = movieJsonNode.get("id").asLong();
                movies.add(movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("No movie found with id: " + movieId)));
            }
            actor.setMovies(movies);
        }
    }
}