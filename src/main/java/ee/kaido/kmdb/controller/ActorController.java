package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.service.ActorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actor")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping("")
    public ResponseEntity<Actor> createActor(@RequestBody Actor actor) {
        return ResponseEntity.ok().body(actorService.addActor(actor));
    }

    @GetMapping("")
    public ResponseEntity<List<Actor>> getActors() {
        return ResponseEntity.ok().body(actorService.getAllActors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable int id) {
        return ResponseEntity.ok().body(actorService.getActorById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable long id, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok().body(actorService.updateActor(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActor(@PathVariable long id) {
        actorService.deleteActor(id);
        return ResponseEntity.ok("Actor with id " + id + " deleted.");
    }
}
