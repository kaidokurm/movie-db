package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("")
    public ResponseEntity<Genre> addGenre(@RequestBody Genre genre) {
        return ResponseEntity.ok().body(genreService.addGenre(genre));
    }

    @GetMapping("")
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok().body(genreService.getAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long id) {
        return ResponseEntity.ok().body(genreService.getGenreById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable long id, @RequestBody Map<String, Object> genre) {
        return ResponseEntity.ok().body(genreService.updateGenre(id, genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<List<Genre>> deleteGenre(@PathVariable long id) {
        return ResponseEntity.ok().body(genreService.deleteGenre(id));
    }
}
