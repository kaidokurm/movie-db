package ee.kaido.kmdb.controller;

import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Movie;
import ee.kaido.kmdb.model.MovieDTO;
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
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.addMovie(movie));
    }

    @GetMapping("movie")
    public ResponseEntity<List<MovieDTO>> getMovieById() {
        List<Movie> movies = movieService.getAllMovies();
//        List<MovieDTO> movieDTOs = new ArrayList<>();
//        for (Movie movie : movies) {
//            MovieDTO newMovie = new MovieDTO(
//                    movie.getId(),
//                    movie.getTitle(),
//                    movie.getReleasedYear(),
//                    movie.getDuration(),
//                    movie.getActors().stream().map(actor -> new ActorDTO(actor.getId(), actor.getName(), actor.getBirthDate())).collect(Collectors.toList())
//            );
        List<MovieDTO> m = movies.stream().map(MovieDTO::new).toList();

        return ResponseEntity.status(HttpStatus.OK).body(m);
    }

//    @GetMapping("movie")
//    public ResponseEntity<List<Movie>> getAllMovies(@RequestParam(required = false) Integer page,
//                                                    @RequestParam(required = false, defaultValue = "20") int size) throws BadRequestException {
//        if (page != null) {
//            if (page < 0 || size < 1) {
//                throw new BadRequestException("Error in Page or Size value");
//            }
//            Pageable pageable = PageRequest.of(page, size);
//            return ResponseEntity.ok().body(movieService.getAllMovies(pageable).getContent());
//        }
//        return ResponseEntity.ok().body(movieService.getAllMovies());
//    }

    @GetMapping("movie/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.findMovieById(id));
    }

    @PatchMapping("movie/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable long id, @RequestBody Map<String, Object> data) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieService.updateMovie(id, data));
    }

    @DeleteMapping("movie/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable long id) {
        return ResponseEntity.ok(movieService.deleteMovie(id));
    }

    @GetMapping({"movies", "movies/search"})
    public ResponseEntity<List<Movie>> getMoviesByFilter(
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String title
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getMoviesByFilter(genreId, releaseYear, actorId, title));
    }

    @GetMapping("movies/{movieId}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(@PathVariable long movieId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(movieService.getActorsInMovie(movieId));
    }
}
