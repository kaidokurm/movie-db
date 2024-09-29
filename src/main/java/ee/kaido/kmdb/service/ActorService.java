package ee.kaido.kmdb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public Actor addActor(Actor actor) throws ElementExistsException {
        validateActor(actor);
        return actorRepository.save(actor);
    }

    private void validateActor(Actor actor) throws ElementExistsException {
        if (actor.getId() != null && actorRepository.findById(actor.getId()).isPresent())
            throw new ElementExistsException("Actor with id " + actor.getId() + " already exists");
        if (actor.getName().trim().isEmpty())
            throw new IllegalArgumentException("Actor name can't be empty!");
        if (actor.getBirthDate() != null) {
            if (!isValidDate(actor.getBirthDate())) {
                throw new ElementExistsException("Actor birth date " + actor.getBirthDate() + " is not valid (yyyy-MM-dd)");
            }
        }
    }

    private boolean isValidDate(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(date.toString());
            return true;
        } catch (ParseException e) {
            return false;
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
            if (name == null || name.trim().isEmpty())
                throw new IllegalArgumentException("Actor name can't be empty!");
            actor.setName(name);
        }
        if (jsonNode.has("birthDate")) {
            try {
                Date birthDate = mapper.convertValue(jsonNode.get("birthDate"), Date.class);
                actor.setBirthDate(birthDate);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid birth date format");
            }
        }
        if (jsonNode.has("movies")) {
            List<Movie> movies = new ArrayList<>();
            JsonNode moviesJsonNode = jsonNode.get("movies");
            for (JsonNode movieJsonNode : moviesJsonNode) {
                Long movieId = movieJsonNode.get("id").asLong();
                if (movieRepository.existsById(movieId)) {
                    movies.add(movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Movie with id " + movieId + " not found")));
                }
            }
            actor.setMovies(movies);
        }
    }

    public List<Actor> deleteActor(Long id) throws ResourceNotFoundException {
        this.getActorById(id);
        actorRepository.deleteById(id);
        return getAllActors();
    }
}