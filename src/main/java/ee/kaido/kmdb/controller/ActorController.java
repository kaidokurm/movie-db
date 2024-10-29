package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.model.MovieDTO;
import ee.kaido.kmdb.service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Create a new actor",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User  created successfully"),
                    @ApiResponse(responseCode = "406", description = "Input not acceptable"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PostMapping("")
    public ResponseEntity<ActorDTO> createActor(@RequestBody Map<String, Object> actor) throws ElementExistsException, BadRequestException, ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actor));
    }

    @GetMapping({"", "/find"})
    public ResponseEntity<List<ActorDTO>> getActorsByFilter(
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok().body(
                actorService.getActorsByFilter(name));
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
        return ResponseEntity.ok().body(
                actorService.updateActor(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        actorService.deleteActor(id, force);
        return ResponseEntity.noContent().build();
    }
}
