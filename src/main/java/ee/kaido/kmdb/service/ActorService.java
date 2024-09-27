package ee.kaido.kmdb.service;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.repository.ActorRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    public Actor addActor(Actor actor) throws ElementExistsException {
        if (actor.getId() != null && actorRepository.findById(actor.getId()).isPresent()) {
            throw new ElementExistsException("Actor with id " + actor.getId() + " already exists");
        }
        if (actor.getName().trim().isEmpty())
            throw new IllegalArgumentException("Actor name can't be empty!");
        return actorRepository.save(actor);
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Actor getActorById(long id) throws ResourceNotFoundException {
        return actorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No actor found with id: " + id));
    }

    public Actor updateActor(Long id, Map<String, Object> data) throws ResourceNotFoundException {

        Actor actor = getActorById(id);
        data.forEach((key, value) -> {
            if (Objects.equals(key, "name") && (value == null || value == ""))
                throw new IllegalArgumentException("Name cannot be empty");

            Field field = ReflectionUtils.findField(Actor.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, actor, value);
        });
        return actorRepository.save(actor);
    }

    public List<Actor> deleteActor(Long id) throws ResourceNotFoundException {
        this.getActorById(id);
        actorRepository.deleteById(id);
        return getAllActors();
    }
}