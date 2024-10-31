package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.MovieService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(
            @Valid @RequestBody MovieDTO movieDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                movieService.createMovie(movieDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(
            @PathVariable long id,
            @RequestParam(required = false, defaultValue = "false") boolean hideActors
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getMovieDtoById(id, hideActors));
    }

    @GetMapping({"/search", ""})
    public ResponseEntity<List<MovieDTO>> getMoviesByFilter(
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long actor,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "false") boolean hideActors
    ) throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(
                movieService.getMoviesByFilter(genre, year, actor, title, page, size, hideActors));
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<ActorDTO>> getActorsByMovie(
            @PathVariable long movieId,
            @RequestParam(required = false) boolean hideMovies
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getActorsInMovie(movieId, hideMovies));
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(
            @PathVariable long id,
            @RequestBody Map<String, Object> data)
            throws ResourceNotFoundException, BadRequestException {
        return ResponseEntity.ok().body(
                movieService.updateMovie(id, data));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable long id) throws ResourceNotFoundException {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
