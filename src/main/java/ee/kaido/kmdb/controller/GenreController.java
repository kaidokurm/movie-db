package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("")
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) throws ElementExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.addGenre(genre));
    }

    @GetMapping("")
    public ResponseEntity<List<Genre>> getAllGenres(@RequestParam(required = false) String name) {
        return ResponseEntity.ok().body(genreService.getAllGenresByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(genreService.getGenreById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody Map<String, Object> genre) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(genreService.updateGenre(id, genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }
}
