package ee.kaido.kmdb;


import ee.kaido.kmdb.controller.MovieController;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovieControllerTest {

    @InjectMocks
    private MovieController movieController;

    @Mock
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMovie_ShouldReturnCreatedMovie() {
        MovieDTO movieDTO = new MovieDTO();
        when(movieService.createMovie(movieDTO)).thenReturn(movieDTO);

        ResponseEntity<MovieDTO> response = movieController.addMovie(movieDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(movieDTO, response.getBody());
        verify(movieService).createMovie(movieDTO);
    }

    @Test
    void getMovieById_ShouldReturnMovie() throws ResourceNotFoundException {
        long movieId = 1L;
        MovieDTO movieDTO = new MovieDTO();
        when(movieService.getMovieDtoById(movieId, true)).thenReturn(movieDTO);

        ResponseEntity<MovieDTO> response = movieController.getMovieById(movieId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movieDTO, response.getBody());
        verify(movieService).getMovieDtoById(movieId, true);
    }

    @Test
    void updateMovie_ShouldReturnUpdatedMovie() throws ResourceNotFoundException, BadRequestException {
        long movieId = 1L;
        HashMap<String, Object> updateData = new HashMap<>();
        MovieDTO updatedMovieDTO = new MovieDTO();
        when(movieService.updateMovie(movieId, updateData)).thenReturn(updatedMovieDTO);

        ResponseEntity<MovieDTO> response = movieController.updateMovie(movieId, updateData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedMovieDTO, response.getBody());
        verify(movieService).updateMovie(movieId, updateData);
    }

    @Test
    void deleteMovie_ShouldReturnNoContent() throws ResourceNotFoundException {
        long movieId = 1L;

        ResponseEntity<Void> response = movieController.deleteMovie(movieId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService).deleteMovie(movieId);
    }

    // Add more tests for other methods and scenarios as needed
}