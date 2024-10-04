package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.service.ActorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actors")
public class ActorController {
    private final ActorService actorService;

    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping("")
    public ResponseEntity<ActorDTO> createActor(@RequestBody Map<String, Object> data) throws ElementExistsException, BadRequestException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.addActor(data));
    }

    @GetMapping("")
    public ResponseEntity<List<ActorDTO>> getActors() {
        return ResponseEntity.ok().body(actorService.getActorsWithMovies());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable int id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(actorService.findActorDtoById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActor(@PathVariable long id, @RequestBody Map<String, Object> data) throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actorService.updateActor(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActor(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(actorService.deleteActor(id));
    }

    @GetMapping("/find")
    public ResponseEntity<List<ActorDTO>> getActorsByName(@RequestParam String name) {
        return ResponseEntity.ok().body(actorService.findActorsByName(name));
    }
}
