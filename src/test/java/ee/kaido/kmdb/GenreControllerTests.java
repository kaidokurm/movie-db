package ee.kaido.kmdb;


import ee.kaido.kmdb.controller.GenreController;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ElementExistsException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.GenreService;
import ee.kaido.kmdb.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreControllerTest {

    @InjectMocks
    private GenreController genreController;

    @Mock
    private GenreService genreService;

    @Mock
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGenre_ShouldReturnCreatedGenre() throws ElementExistsException {
        Genre genre = new Genre();
        when(genreService.addGenre(genre)).thenReturn(genre);

        ResponseEntity<Genre> response = genreController.createGenre(genre);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(genre, response.getBody());
        verify(genreService).addGenre(genre);
    }

    @Test
    void getGenreById_ShouldReturnGenre() {
        long genreId = 1L;
        Genre genre = new Genre();
        when(genreService.getGenreById(genreId)).thenReturn(Optional.of(genre));

        ResponseEntity<Optional<Genre>> response = genreController.getGenreById(genreId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(genre), response.getBody());
        verify(genreService).getGenreById(genreId);
    }

    @Test
    void getGenreMovies_ShouldReturnMovies() throws ResourceNotFoundException {
        long genreId = 1L;
        List<MovieDTO> movies = List.of(new MovieDTO());
        when(movieService.getMoviesByFilter(genreId, null, null, null, null, null, false)).thenReturn(movies);

        ResponseEntity<List<MovieDTO>> response = genreController.getGenreMovies(genreId, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movies, response.getBody());
        verify(movieService).getMoviesByFilter(genreId, null, null, null, null, null, false);
    }

    @Test
    void updateGenre_ShouldReturnUpdatedGenre() throws ResourceNotFoundException {
        long genreId = 1L;
        HashMap<String, Object> updateData = new HashMap<>();
        Genre updatedGenre = new Genre();
        when(genreService.updateGenre(genreId, updateData)).thenReturn(updatedGenre);

        ResponseEntity<Genre> response = genreController.updateGenre(genreId, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedGenre, response.getBody());
        verify(genreService).updateGenre(genreId, updateData);
    }

    @Test
    void deleteGenre_ShouldReturnNoContent() throws ResourceNotFoundException, BadRequestException {
        long genreId = 1L;

        ResponseEntity<Void> response = genreController.deleteGenre(genreId, false);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(genreService).deleteGenre(genreId, false);
    }

    // Add more tests for other methods and scenarios as needed
}