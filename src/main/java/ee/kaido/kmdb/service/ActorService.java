package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Actor addActor(Map<String, Object> data) throws ElementExistsException, ResourceNotFoundException, BadRequestException {
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        if (jsonNode.has("id"))
            checkIfActorIdExists(jsonNode.get("id").asLong());
        Actor actor = new Actor();
        updateActorFields(jsonNode, actor);
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

    private static Date checkActorBirthDate(String birthDate) throws BadRequestException {
        if (birthDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            try {
                LocalDate localDate = LocalDate.parse(birthDate, formatter);
                return Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
            } catch (DateTimeParseException e) {
                throw new BadRequestException(birthDate + " is not a valid date format 'yyyy-MM-dd'");
            }
        }
        return null;
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

    public Actor updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        Actor actor = getActorById(id);
        JsonNode jsonNode = mapper.convertValue(data, JsonNode.class);
        updateActorFields(jsonNode, actor);
        return actorRepository.save(actor);
    }

    private void updateActorFields(JsonNode jsonNode, Actor actor) throws ResourceNotFoundException, BadRequestException {
        if (jsonNode.has("name")) {
            String name = jsonNode.get("name").asText();
            checkActorName(name);
            actor.setName(name);
        }
        if (jsonNode.has("birthDate")) {
            String birthDateString = jsonNode.get("birthDate").asText();
            Date date = checkActorBirthDate(birthDateString);
            actor.setBirthDate(date);
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