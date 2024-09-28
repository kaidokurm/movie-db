package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("movie")
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.addMovie(movie));
    }

    @GetMapping("movie")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok().body(movieService.getAllMovies());
    }

    @GetMapping("movie/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getMovieById(id));
    }

    @PatchMapping("movie/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable long id, @RequestBody Map<String, Object> data) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieService.updateMovie(id, data));
    }

    @DeleteMapping("movie/{id}")
    public ResponseEntity<List<Movie>> deleteMovie(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieService.deleteMovie(id));
    }

    @GetMapping("movies")
    public ResponseEntity<List<Movie>> getMoviesByFilter(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Long actorId
    ) {
        return ResponseEntity.ok().body(movieService.getMoviesByFilter(genreId, releaseYear, actorId));
    }
}
