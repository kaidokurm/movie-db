package ee.kaido.kmdb.service;

import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    public Actor addActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Actor getActorById(long id) {
        return actorRepository.findById(id).orElseThrow(() -> new RuntimeException("No actor found with id: " + id));
    }

    public Actor updateActor(Long id, Map<String, Object> data) {

        Actor actor = getActorById(id);
        data.forEach((key, value) -> {

            Field field = ReflectionUtils.findField(Actor.class, key);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, actor, value);
        });

        return actorRepository.save(actor);
    }

    public void deleteActor(Long id) {
        actorRepository.deleteActorById(id);
    }
}
