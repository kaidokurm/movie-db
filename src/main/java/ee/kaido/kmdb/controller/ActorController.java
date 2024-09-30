package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.service.ActorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping("actor")
    public ResponseEntity<Actor> createActor(@RequestBody Actor actor) throws ElementExistsException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.addActor(actor));
    }

    @GetMapping("actor")
    public ResponseEntity<List<Actor>> getActors() {
        return ResponseEntity.ok().body(actorService.getAllActors());
    }

    @GetMapping("actor/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable int id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(actorService.getActorById(id));
    }

    @PatchMapping("actor/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable long id, @RequestBody Map<String, Object> data) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actorService.updateActor(id, data));
    }

    @DeleteMapping("actor/{id}")
    public ResponseEntity<List<Actor>> deleteActor(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actorService.deleteActor(id));
    }

    @GetMapping("actors")
    public ResponseEntity<List<Actor>> getActorsByName(@RequestParam String name) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(actorService.findActorsByName(name));
    }
}
