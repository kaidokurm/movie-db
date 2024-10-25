package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.ActorDTO;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.model.MovieDTO;
import ee.kaido.kmdb.service.MovieService;
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

    @PostMapping
    public ResponseEntity<MovieDTO> addMovie(
            @RequestBody Movie movie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                movieService.addMovie(movie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovie(
            @PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getMovieById(id));
    }

    @GetMapping({"/search", ""})
    public ResponseEntity<List<MovieDTO>> getMoviesByFilter(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getMoviesByFilter(genreId, releaseYear, actorId, title, page, size));
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<ActorDTO>> getActorsByMovie(
            @PathVariable long movieId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.getActorsInMovie(movieId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(
            @PathVariable long id,
            @RequestBody Map<String, Object> data)
            throws ResourceNotFoundException {
        return ResponseEntity.ok().body(
                movieService.updateMovie(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(
            @PathVariable long id) throws ResourceNotFoundException {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
