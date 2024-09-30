package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final ObjectMapper mapper;
    private final MovieService movieService;

    public ActorService(ActorRepository actorRepository, ObjectMapper mapper, MovieService movieService) {
        this.actorRepository = actorRepository;
        this.mapper = mapper;
        this.movieService = movieService;
    }

    public Actor addActor(Actor actor) throws ElementExistsException, ResourceNotFoundException {
        checkIfActorIdExists(actor.getId());
        checkActorName(actor.getName());
        checkActorBirthDate(actor.getBirthDate());
        checkActorMovies(actor);
        return actorRepository.save(actor);
    }


    private void checkIfActorIdExists(Long id) throws ElementExistsException {
        if (id != null && actorRepository.findById(id).isPresent())
            throw new ElementExistsException("Actor with id " + id + " already exists");
    }

    private static void checkActorName(String name) {
        if (name.trim().isEmpty())
            throw new IllegalArgumentException("Actor name can't be empty!");
    }

    private void checkActorBirthDate(Date birthDate) {
        if (birthDate != null) {
            try {
                Instant instant = birthDate.toInstant();
                LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                formatter.parse(localDate.toString());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(birthDate + " is not a valid date format 'yyyy-MM-dd'");
            }
        }
    }

    private void checkActorMovies(Actor actor) throws ResourceNotFoundException {
        if (actor.getMovies() != null && !actor.getMovies().isEmpty()) {
            List<Movie> movies = new ArrayList<>();
            for (Movie movie : actor.getMovies()) {
                movies.add(movieService.findMovieById(movie.getId()));
            }
            actor.setMovies(movies);
        }
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Actor getActorById(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public Actor updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException {
        Actor actor = getActorById(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        return actorRepository.save(actor);
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws ResourceNotFoundException {
        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asText();
            checkActorName(name);
            actor.setName(name);
        }
        if (jsonNode.has("birthDate")) {
            Date birthDate = mapper.convertValue(jsonNode.get("birthDate"), Date.class);
            checkActorBirthDate(birthDate);
            actor.setBirthDate(birthDate);
        }
        if (jsonNode.has("movies")) {
            List<Movie> movies = new ArrayList<>();
            JsonNode moviesJsonNode = jsonNode.get("movies");
            for (JsonNode movieJsonNode : moviesJsonNode) {
                Long movieId = movieJsonNode.get("id").asLong();
                movies.add(movieService.findMovieById(movieId));
            }
            actor.setMovies(movies);
            checkActorMovies(actor);
        }
    }

    public List<Actor> deleteActor(Long id) throws ResourceNotFoundException {
        this.getActorById(id);
        actorRepository.deleteById(id);
        return getAllActors();
    }

    public List<Actor> findActorsByName(String name) {
        return actorRepository.findByNameContains(name);
    }
}