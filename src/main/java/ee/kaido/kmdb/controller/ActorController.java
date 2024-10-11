package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.model.MovieDTO;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(data));
    }

    @GetMapping({"", "/find"})
    public ResponseEntity<List<ActorDTO>> getActorsByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "true") boolean showMovies) {
        return ResponseEntity.ok().body(
                actorService.getActorsByFilter(name, showMovies));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(
            @PathVariable long id)
            throws ResourceNotFoundException {
        System.out.println("getMappingId");
        return ResponseEntity.ok().body(
                actorService.findActorDtoById(id));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getActorMovies(
            @PathVariable long id) {
        return ResponseEntity.ok().body(
                actorService.getActorMovies(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActor(
            @PathVariable long id,
            @RequestBody Map<String, Object> data)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                actorService.updateActor(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActor(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                actorService.deleteActor(id, force));
    }
}
