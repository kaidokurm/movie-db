package ee.kaido.kmdb;

import ee.kaido.kmdb.controller.ActorController;
import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.ActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActorControllerTest {

    @InjectMocks
    private ActorController actorController;

    @Mock
    private ActorService actorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createActor_ShouldReturnCreatedActor() throws BadRequestException {
        ActorDTO actorDTO = new ActorDTO();
        when(actorService.createActor(actorDTO)).thenReturn(actorDTO);

        ResponseEntity<ActorDTO> response = actorController.createActor(actorDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(actorDTO, response.getBody());
        verify(actorService).createActor(actorDTO);
    }

    @Test
    void getActorById_ShouldReturnActor() throws ResourceNotFoundException {
        long actorId = 1L;
        ActorDTO actorDTO = new ActorDTO();
        when(actorService.findActorDtoById(actorId, true)).thenReturn(actorDTO);

        ResponseEntity<ActorDTO> response = actorController.getActorById(actorId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(actorDTO, response.getBody());
        verify(actorService).findActorDtoById(actorId, true);
    }

    @Test
    void getActorMovies_ShouldReturnMovies() throws ResourceNotFoundException {
        long actorId = 1L;
        List<MovieDTO> movies = List.of(new MovieDTO());
        when(actorService.getActorMovies(actorId)).thenReturn(movies);

        ResponseEntity<List<MovieDTO>> response = actorController.getActorMovies(actorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(movies, response.getBody());
        verify(actorService).getActorMovies(actorId);
    }

    @Test
    void updateActor_ShouldReturnUpdatedActor() throws ResourceNotFoundException, BadRequestException {
        long actorId = 1L;
        HashMap<String, Object> updateData = new HashMap<>();
        ActorDTO updatedActorDTO = new ActorDTO();
        when(actorService.updateActor(actorId, updateData, true)).thenReturn(updatedActorDTO);

        ResponseEntity<ActorDTO> response = actorController.updateActor(actorId, updateData, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedActorDTO, response.getBody());
        verify(actorService).updateActor(actorId, updateData, true);
    }

    @Test
    void deleteActor_ShouldReturnNoContent() throws ResourceNotFoundException, BadRequestException {
        long actorId = 1L;

        ResponseEntity<Void> response = actorController.deleteActor(actorId, false);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(actorService).deleteActor(actorId, false);
    }

    // Add more tests for other methods and scenarios as needed
}