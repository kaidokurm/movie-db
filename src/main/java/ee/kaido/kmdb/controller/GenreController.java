package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ElementExistsException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.GenreService;
import ee.kaido.kmdb.service.MovieService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;
    private final MovieService movieService;

    public GenreController(GenreService genreService, MovieService movieService) {
        this.genreService = genreService;
        this.movieService = movieService;
    }

    @Transactional
    @PostMapping("")
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre genre)
            throws ElementExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                genreService.createGenre(genre));
    }

    @GetMapping("")
    public ResponseEntity<List<Genre>> getAllGenres(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws BadRequestException {
        return ResponseEntity.ok().body(
                genreService.getAllGenresByName(name, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                genreService.getGenreByIdOrThrowError(id));
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getGenreMovies(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean hideActor)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(
                movieService.getMoviesByFilter(
                        id,
                        null,
                        null,
                        null,
                        null,
                        null,
                        hideActor));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable Long id,
            @RequestBody Map<String, Object> genre
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                genreService.updateGenre(id, genre));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean force)
            throws ResourceNotFoundException, BadRequestException {
        genreService.deleteGenre(id, force);
        return ResponseEntity.noContent().build();
    }
}
