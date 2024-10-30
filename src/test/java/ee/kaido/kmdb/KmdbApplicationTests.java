package ee.kaido.kmdb;

import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.repository.ActorRepository;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KmdbApplicationTests {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void contextLoads() {
        assertNotNull(movieRepository);
        assertNotNull(actorRepository);
        assertNotNull(genreRepository);
    }

    @Test
    @Transactional
    void testCreateAndRetrieveMovie() throws BadRequestException {
        // Create genre
        Genre actionGenre = new Genre();
        actionGenre.setName("Action");
        genreRepository.save(actionGenre);
        System.out.println(actionGenre.getId());

        // Create actor
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
        movie.setGenres(Set.of(actionGenre));
        movie.setActors(List.of(actor));

        Movie savedMovie = movieRepository.save(movie);

        // Retrieve and verify
        Movie retrievedMovie = movieRepository.findById(savedMovie.getId()).orElse(null);
        assertNotNull(retrievedMovie);
        assertEquals("Test Movie", retrievedMovie.getTitle());
        assertEquals(2023, retrievedMovie.getReleaseYear());
        assertEquals(Duration.ofMinutes(120), retrievedMovie.getDuration());
        assertEquals(1, retrievedMovie.getGenres().size());
//        assertEquals(actionGenre.getId(), retrievedMovie.getGenres().getId());
        assertEquals(1, retrievedMovie.getActors().size());
    }

    @Test
    @Transactional
    void testFindActorsByName() throws BadRequestException {
        Actor actor = new Actor();
        actor.setName("Tom Hanks");
        actor.setBirthDate("1956-07-09");
        actorRepository.save(actor);

        List<Actor> foundActors = actorRepository.findByNameContains("tom");
        assertFalse(foundActors.isEmpty());
        assertTrue(foundActors.stream().anyMatch(a -> a.getName().equalsIgnoreCase("Tom Hanks")));
    }

    @Test
    @Transactional
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
    void testUpdateMovie() {
        Movie movie = new Movie();
        movie.setTitle("Original Title");
        movie.setReleaseYear(2023);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        Movie savedMovie = movieRepository.save(movie);

        // Update movie
        savedMovie.setTitle("Updated Title");
        movieRepository.save(savedMovie);

        Movie updatedMovie = movieRepository.findById(savedMovie.getId()).orElse(null);
        assertNotNull(updatedMovie);
        assertEquals("Updated Title", updatedMovie.getTitle());
    }

    @Test
    @Transactional
    void testDeleteMovie() {
        Movie movie = new Movie();
        movie.setTitle("To Be Deleted");
        movie.setReleaseYear(2023);
        Duration duration = Duration.ofMinutes(120);
        movie.setDuration(duration);
        Movie savedMovie = movieRepository.save(movie);

        movieRepository.deleteById(savedMovie.getId());

        assertFalse(movieRepository.findById(savedMovie.getId()).isPresent());
    }

    @Test
    @Transactional
    void testMovieValidation() {
        Movie movie = new Movie();
        // Don't set required fields
        assertThrows(Exception.class, () -> movieRepository.save(movie));
    }
}

