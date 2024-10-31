package ee.kaido.kmdb;

import ee.kaido.kmdb.controller.MovieController;
import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import ee.kaido.kmdb.service.MovieService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieControllerTests {

    @Autowired
    private MovieService movieService;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ActorRepository actorRepository;
    private MovieController movieController;


    @BeforeEach
    void setUp() {
        movieController = new MovieController(movieService);
    }

    @Test
    @Transactional
    void testCreateMovie() {
        Duration duration = Duration.ofHours(2);
        MovieDTO movie = new MovieDTO(null, "New Movie", 2023, duration, null, null);

        ResponseEntity<MovieDTO> responseEntity = movieController.addMovie(movie);
        MovieDTO createdMovie = responseEntity.getBody();

        assertNotNull(createdMovie);
        assertNotNull(createdMovie.getId());
        assertEquals("New Movie", createdMovie.getTitle());
    }

    @Test
    @Transactional
    void testGetMovieById() throws ResourceNotFoundException {
        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre(null, "Genre");
        genres.add(genre);

        List<Actor> actors = new ArrayList<>();
        Actor actor = new Actor(null, "Actor", new Date(), null);
        actors.add(actor);

        Movie movie1 = new Movie(null, "Test Movie", 2020, Duration.ofHours(1), genres, actors);
        movie1 = movieRepository.save(movie1);
        MovieDTO movie = new MovieController(movieService).addMovie(new MovieDTO(movie1, false)).getBody();
        ResponseEntity<MovieDTO> responseEntity = movieController.getMovieById(movie.getId(), false);
        MovieDTO foundMovie = responseEntity.getBody();

        assertNotNull(foundMovie);
        assertEquals(movie.getTitle(), foundMovie.getTitle());

        assertEquals("Test Movie", foundMovie.getTitle());
        assertEquals(2020, foundMovie.getReleaseYear());
        assertEquals(Duration.ofMinutes(60), foundMovie.getDuration());
        assertEquals(1, foundMovie.getGenres().size());
        assertTrue(foundMovie.getGenres().contains(genre));
        assertFalse(foundMovie.getActors().isEmpty());
        assertTrue(foundMovie.getActors().contains(new ActorDTO(actor, true)));
    }

    @Test
    @Transactional
    void getMoviesFiltered() throws BadRequestException, ResourceNotFoundException {
        // Create genres
        Genre genre = new Genre();
        genre.setName("Test Action Genre");
        genre = genreRepository.save(genre);

        // Create actors
        Actor actor = new Actor();
        actor.setName("Test Actor");
        actor.setBirthDate("1980-01-01");
        actorRepository.save(actor);

        // Create movie
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setReleaseYear(2023);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        movie.setGenres(Set.of(genre));
        movie.setActors(List.of(actor));

        movieRepository.save(movie);

        //getMovies filtered
        ResponseEntity<List<MovieDTO>> moviesF = new MovieController(movieService).getMoviesByFilter(
                genre.getId(),
                2023, actor.getId(), "t", null, null, false);
        System.out.println(moviesF.getBody());
        assertFalse(moviesF.getBody().isEmpty());
        assertEquals(1, moviesF.getBody().size());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testNoMovieFoundWithTitle() throws BadRequestException, ResourceNotFoundException {

        //no movie found with title
        ResponseEntity<List<MovieDTO>> moviesF1 = new MovieController(movieService).getMoviesByFilter(
                null,
                2023, null, "t2Asa!@", null, null, false);
        System.out.println(moviesF1.getBody());
        assertTrue(moviesF1.getBody().isEmpty());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testGetMovieNoActorFound() {
        assertThrows(ResourceNotFoundException.class, () -> new MovieController(movieService).getMoviesByFilter(
                null, 2023, -1L, "t", null, null, false));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testGetMovieYear() throws BadRequestException, ResourceNotFoundException {
        ResponseEntity<List<MovieDTO>> moviesF3 = new MovieController(movieService).getMoviesByFilter(
                null, 1500, null, "t", null, null, false);
        assertTrue(moviesF3.getBody().isEmpty());
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testMovieNoGenreFound() {
        assertThrows(ResourceNotFoundException.class, () ->
                new MovieController(movieService).getMoviesByFilter(-1L,
                        2023, null, "t", null, null, false));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testMovieWrongPadding() {

        assertThrows(BadRequestException.class, () ->
                new MovieController(movieService).getMoviesByFilter(null,
                        null, null, null, -1, 0, false));
    }

    @Test
    @org.springframework.transaction.annotation.Transactional
    void testFindMoviesByGenre() {
        Genre sciFiGenre = new Genre();
        sciFiGenre.setName("Science Fiction");
        genreRepository.save(sciFiGenre);

        Movie movie = new Movie();
        movie.setTitle("Inception");
        movie.setReleaseYear(2010);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        movie.setGenres(Set.of(sciFiGenre));
        movieRepository.save(movie);

        List<Movie> sciFiMovies = movieRepository.getMoviesByFilters(sciFiGenre, null, null, null);
        assertFalse(sciFiMovies.isEmpty());
        assertTrue(sciFiMovies.stream().anyMatch(m -> m.getTitle().equals("Inception")));
    }

    @Test
    @Transactional
    void testUpdateMovie() throws ResourceNotFoundException, BadRequestException {
        MovieDTO movie = movieService.createMovie(new MovieDTO(null, "Old Movie", 2020, Duration.ofHours(1), null, null));
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Updated Movie");

        ResponseEntity<MovieDTO> responseEntity = movieController.updateMovie(movie.getId(), updates);
        MovieDTO updatedMovie = responseEntity.getBody();

        assertNotNull(updatedMovie);
        assertEquals("Updated Movie", updatedMovie.getTitle());
    }

    @Test
    @Transactional
    void testDeleteMovie() throws ResourceNotFoundException, BadRequestException {
        MovieDTO movieDTO = new MovieDTO(null, "Movie to Delete", 2020, Duration.ofHours(1), null, null);
        MovieDTO movie = movieService.createMovie(movieDTO);

        // Delete the movie
        movieController.deleteMovie(movie.getId());

        // Verify that the movie has been deleted
        assertThrows(ResourceNotFoundException.class, () -> {
            movieController.getMovieById(movie.getId(), false);
        });
    }

    @Test
    @Transactional
    void testGetMovieActors() throws ResourceNotFoundException, BadRequestException {
        Actor actor = new Actor();
        actor.setName("Aa");
        actor.setBirthDate("1921-11-30");
        List<Actor> actors = new ArrayList<>();
        actors.add(actor);
        Movie m = new Movie();
        m.setTitle("Movie with Actors");
        m.setDuration(Duration.ofHours(2));
        m.setReleaseYear(2020);
        m.setActors(actors);
        movieRepository.save(m);
        // Assume that movie has actors associated
        ResponseEntity<List<ActorDTO>> responseEntity = movieController.getActorsByMovie(m.getId(), false);
        List<ActorDTO> bodyActors = responseEntity.getBody();

        assertNotNull(bodyActors);
        assertFalse(bodyActors.isEmpty());
    }
}