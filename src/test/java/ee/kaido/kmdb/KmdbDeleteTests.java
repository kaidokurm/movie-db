package ee.kaido.kmdb;

import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ElementExistsException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import ee.kaido.kmdb.service.ActorService;
import ee.kaido.kmdb.service.GenreService;
import ee.kaido.kmdb.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class KmdbDeleteTests {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Genre genre = new Genre();
    private final Actor actor = new Actor();
    private final Movie movie = new Movie();
    private ActorDTO actorDTO = new ActorDTO();
    private MovieDTO movieDTO = new MovieDTO();
    @Autowired
    private GenreService genreService;
    @Autowired
    private ActorService actorService;
    @Autowired
    private MovieService movieService;


    @BeforeEach
    void setUp() throws ElementExistsException, BadRequestException {
        // Create and save a genre
        genre.setName("Test Action");
        genre = genreService.addGenre(genre);

        // Create and save an actor
        actor.setName("Test Actor");
        actor.setBirthDate("1999-11-01");
        actorDTO = new ActorDTO(actor, false);
        actorDTO = actorService.createActor(actorDTO);

        // Create and save a movie
        movie.setTitle("Test Movie");
        movie.setReleaseYear(2023);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        movieDTO = new MovieDTO(movie, true);
        movieDTO.setGenres(Set.of(genre));
        movieDTO.setActors(List.of(actorDTO));
        movieDTO = movieService.createMovie(movieDTO);
    }

    @Test
    @Transactional
    void testDeleteGenreNoForce() {
        // Attempt to delete the genre without force
        assertThrows(BadRequestException.class, () -> genreService.deleteGenre(genre.getId(), false));
        assertThrows(ResourceNotFoundException.class, () -> genreService.deleteGenre(-9999999L, false));
    }

    @Test
    @Transactional
    void testDeleteActorNoForce() {
        // Attempt to delete the actor
        assertThrows(BadRequestException.class, () -> actorService.deleteActor(actorDTO.getId(), false));
        assertThrows(ResourceNotFoundException.class, () -> actorService.deleteActor(-9999999L, false));
    }

    @Test
    @Transactional
    void testDeleteGenreForce() throws BadRequestException, ResourceNotFoundException {
        // Now delete with force (assuming your repository supports this)
        genreService.deleteGenre(genre.getId(), true);

        // Verify that the genre is deleted
        assertFalse(genreRepository.findById(genre.getId()).isPresent());
    }


    @Test
    @Transactional
    void testDeleteActorForce() throws BadRequestException, ResourceNotFoundException {
        // Now delete with force (assuming your repository supports this)
        actorService.deleteActor(actorDTO.getId(), true);

        // Verify that the actor is deleted
        assertFalse(actorRepository.findById(actorDTO.getId()).isPresent());
    }

    @Test
    @Transactional
    void testDeleteMovie() throws ResourceNotFoundException {
        // Attempt to delete the movie
        movieService.deleteMovie(movieDTO.getId());

        // Verify that the movie is deleted
        assertFalse(movieRepository.findById(movieDTO.getId()).isPresent());
    }
}