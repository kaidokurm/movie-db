package ee.kaido.kmdb;

import ee.kaido.kmdb.controller.GenreController;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ElementExistsException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.GenreRepository;
import ee.kaido.kmdb.repository.MovieRepository;
import ee.kaido.kmdb.service.GenreService;
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
class GenreControllerTests {

    @Autowired
    private GenreService genreService;
    @Autowired
    private MovieService movieService;
    private GenreController genreController;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        genreController = new GenreController(genreService, movieService);
    }

    @Test
    @Transactional
    void testCreateGenre() throws ElementExistsException {
        Genre genre = new Genre();
        genre.setName("Genre X");

        ResponseEntity<Genre> responseEntity = genreController.createGenre(genre);
        Genre createdGenre = responseEntity.getBody();

        assertNotNull(createdGenre);
        assertNotNull(createdGenre.getId());
        assertEquals("Genre X", createdGenre.getName());
    }

    @Test
    @Transactional
    void testGetAllGenres() throws BadRequestException {
        genreRepository.save(new Genre(null, "000"));
        ResponseEntity<List<Genre>> responseEntity = genreController.getAllGenres("0", 0, 200);
        List<Genre> genres = responseEntity.getBody();

        assertNotNull(genres);
        assertFalse(genres.isEmpty());
    }

    @Test
    @Transactional
    void testGetGenreById() throws ResourceNotFoundException, ElementExistsException {
        Genre genre = genreService.createGenre(new Genre(null, "Genre Y"));
        ResponseEntity<Genre> responseEntity = genreController.getGenreById(genre.getId());
        Genre foundGenre = responseEntity.getBody();

        assertNotNull(foundGenre);
        assertEquals(genre.getName(), foundGenre.getName());
    }

    @Test
    @Transactional
    void testGetGenreMovies() throws ResourceNotFoundException, ElementExistsException, BadRequestException {
        Genre genre = genreService.createGenre(new Genre(null, "Genre Y"));
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        Movie movie = new Movie(null, "Nimi", 1982, Duration.ofMinutes(120), genres, null);
        movieRepository.save(movie);
        MovieDTO movieDTO = new MovieDTO(movie, true);
        ResponseEntity<List<MovieDTO>> responseEntity = genreController.getGenreMovies(genre.getId(), true);
        List<MovieDTO> movieDTOS = responseEntity.getBody();

        assertNotNull(movieDTOS);
        assertTrue(movieDTOS.contains(movieDTO));
    }

    @Test
    @Transactional
    void testUpdateGenre() throws ResourceNotFoundException, ElementExistsException {
        Genre genre = genreService.createGenre(new Genre(null, "Genre Z"));
        genreRepository.save(genre);
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Genre Z");

        ResponseEntity<Genre> responseEntity = genreController.updateGenre(genre.getId(), updates);
        Genre updatedGenre = responseEntity.getBody();

        assertNotNull(updatedGenre);
        assertEquals("Updated Genre Z", updatedGenre.getName());
    }

    @Test
    @Transactional
    void testDeleteGenre() throws ResourceNotFoundException, ElementExistsException, BadRequestException {
        Genre genre = genreService.createGenre(new Genre(null, "Genre A"));

        genreController.deleteGenre(genre.getId(), true);

        assertThrows(ResourceNotFoundException.class, () -> {
            genreController.getGenreById(genre.getId());
        });
    }
}