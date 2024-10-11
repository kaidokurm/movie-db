package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.BadRequestException;
import ee.kaido.kmdb.controller.exception.ElementExistsException;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.model.MovieDTO;
import ee.kaido.kmdb.service.GenreService;
import ee.kaido.kmdb.service.MovieService;
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
    private final MovieService movieService;

    public GenreController(GenreService genreService, MovieService movieService) {
        this.genreService = genreService;
        this.movieService = movieService;
    }

    @PostMapping("")
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) throws ElementExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                genreService.addGenre(genre));
    }

    @GetMapping("")
    public ResponseEntity<List<Genre>> getAllGenres(@RequestParam(required = false) String name) {
        return ResponseEntity.ok().body(
                genreService.getAllGenresByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                genreService.getGenreByIdOrThrowError(id));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getGenreMovies(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getMoviesByFilter(id, null, null, null,null,null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody Map<String, Object> genre) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                genreService.updateGenre(id, genre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok(
                genreService.deleteGenre(id, force));
    }
}
