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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.kaido.kmdb.service.checkers.Checks.checkIfStringNotEmpty;
import static ee.kaido.kmdb.service.checkers.Checks.checkIsIsoDate;

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

        return new ActorDTO(savedActor);
    }

    public List<ActorDTO> getActorsWithMovies() {
        List<Actor> actors = actorRepository.findAll();
        List<ActorDTO> actorDTOs = new ArrayList<>();
        for (Actor actor : actors) {
            actorDTOs.add(new ActorDTO(actor));
        }
        return actorDTOs;
    }

    private void checkIfActorIdExists(Long id) throws ElementExistsException {
        if (id != null && actorRepository.findById(id).isPresent())
            throw new ElementExistsException("Actor with id " + id + " already exists");
    }

    public ActorDTO findActorDtoById(long id) throws ResourceNotFoundException {
        return new ActorDTO(actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id)));
    }

    public Actor findActorById(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public ActorDTO updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        Actor actor = findActorById(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        Actor savedActor = actorRepository.save(actor);
        return new ActorDTO(savedActor);
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws BadRequestException, ResourceNotFoundException {
        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asText();
            checkIfStringNotEmpty(name, "Actor name");
            actor.setName(name);
        }
        if (jsonNode.has("birthDate")) {
            String birthDateString = jsonNode.get("birthDate").asText();
            Date date = checkIsIsoDate(birthDateString);
            actor.setBirthDate(birthDateString);
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

    public String deleteActor(Long id) throws ResourceNotFoundException {
        this.findActorById(id);
        actorRepository.deleteById(id);
        return "Actor with id " + id + " was deleted";
    }

    public List<ActorDTO> findActorsByName(String name) {
        return actorRepository.findByNameContains(name).stream().map(ActorDTO::new).collect(Collectors.toList());
    }
}