package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.ActorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

    @Transactional
    // for swagger
    @Operation(summary = "Create a new actor",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User  created successfully"),
                    @ApiResponse(responseCode = "406", description = "Input not acceptable"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PostMapping("")
    public ResponseEntity<ActorDTO> createActor(@Valid @RequestBody ActorDTO actor)
            throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED).body(actorService.createActor(actor));
    }

    @GetMapping({"", "/find"})
    public ResponseEntity<List<ActorDTO>> getActorsByFilter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean hideMovies
    ) throws BadRequestException {
        return ResponseEntity.ok().body(
                actorService.getActorsByFilter(name, page, size, hideMovies));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean hideMovies)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                actorService.findActorDtoById(id, hideMovies));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getActorMovies(
            @PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                actorService.getActorMovies(id));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<ActorDTO> updateActor(
            @PathVariable long id,
            @RequestBody Map<String, Object> data,
            @RequestParam(required = false, defaultValue = "false") boolean hideMovies)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(
                actorService.updateActor(id, data, hideMovies));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        actorService.deleteActor(id, force);
        return ResponseEntity.noContent().build();
    }
}
