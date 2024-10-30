package ee.kaido.kmdb;

import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class KmdbDeleteTests {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Genre genre;
    private Actor actor;
    private Movie movie;

    @BeforeEach
    void setUp() {
        // Create and save a genre
        genre = new Genre();
        genre.setName("Action");
        genreRepository.save(genre);

        // Create and save an actor
        actor = new Actor();
        actor.setName("Test Actor");
        actorRepository.save(actor);

        // Create and save a movie
        movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setReleaseYear(2023);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        movie.setGenres(Set.of(genre));
        movie.setActors(List.of(actor));
        movieRepository.save(movie);
    }

    @Test
    @Transactional
    void testDeleteGenreNoForce() {
        System.out.println(genre);
        // Attempt to delete the genre without force
        genreRepository.deleteById(genre.getId());
        System.out.println(genre);
        // Verify that the genre is deleted
        assertFalse(genreRepository.findById(genre.getId()).isPresent());
    }

    @Test
    @Transactional
    void testDeleteGenreForce() {
        // Save the genre again to ensure it exists
        genreRepository.save(genre);

        // Now delete with force (assuming your repository supports this)
        genreRepository.deleteById(genre.getId());

        // Verify that the genre is deleted
        assertFalse(genreRepository.findById(genre.getId()).isPresent());
    }

    @Test
    @Transactional
    void testDeleteActor() {
        // Attempt to delete the actor
        actorRepository.deleteById(actor.getId());

        // Verify that the actor is deleted
        assertFalse(actorRepository.findById(actor.getId()).isPresent());
    }

    @Test
    @Transactional
    void testDeleteActorForce() {
        // Save the actor again to ensure it exists
        actorRepository.save(actor);

        // Now delete with force (assuming your repository supports this)
        actorRepository.deleteById(actor.getId());

        // Verify that the actor is deleted
        assertFalse(actorRepository.findById(actor.getId()).isPresent());
    }

    @Test
    @Transactional
    void testDeleteMovie() {
        // Attempt to delete the movie
        movieRepository.deleteById(movie.getId());

        // Verify that the movie is deleted
        assertFalse(movieRepository.findById(movie.getId()).isPresent());
    }
}