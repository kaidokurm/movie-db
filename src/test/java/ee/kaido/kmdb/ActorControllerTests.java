package ee.kaido.kmdb;

import ee.kaido.kmdb.controller.ActorController;
import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.dto.MovieDTO;
import ee.kaido.kmdb.entity.Movie;
import ee.kaido.kmdb.exception.BadRequestException;
import ee.kaido.kmdb.exception.ResourceNotFoundException;
import ee.kaido.kmdb.repository.MovieRepository;
import ee.kaido.kmdb.service.ActorService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActorControllerTests {

    @Autowired
    private ActorService actorService;

    private ActorController actorController;
    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        actorController = new ActorController(actorService);
    }

    @Test
    @Transactional
    void testCreateActor() throws BadRequestException {
        ActorDTO actor = new ActorDTO();
        actor.setName("Aa");
        actor.setBirthDate("1921-11-30");
        Duration duration = Duration.ofHours(1);
        MovieDTO movie = new MovieDTO(1L, "Movie", 1982, duration, null, null);
        movieRepository.save(new Movie(movie));
        actor.setMovies(List.of(movie));

        ResponseEntity<ActorDTO> responseEntity = actorController.createActor(actor);
        ActorDTO createdActor = responseEntity.getBody();
        System.out.println(createdActor);
        assertNotNull(createdActor);
        assertNotNull(createdActor.getId());
        assertEquals("Aa", createdActor.getName());
        assertEquals("1921-11-30", createdActor.getBirthDate());
        assertFalse(createdActor.getMovies().isEmpty());
    }

    @Test
    @Transactional
    void testGetActorById() throws ResourceNotFoundException, BadRequestException {
        ActorDTO actorDTO = new ActorDTO(null, "Actor Test", "2000-01-01", null);
        ActorDTO actor = actorService.createActor(actorDTO);
        ResponseEntity<ActorDTO> responseEntity = actorController.getActorById(actor.getId(), true);
        ActorDTO foundActor = responseEntity.getBody();

        assertNotNull(foundActor);
        assertEquals(actor.getName(), foundActor.getName());
    }

    @Test
    @Transactional
    void testFilterActorByName() throws BadRequestException {
        ActorDTO actorDTO = new ActorDTO(null, "Actor Test!@#", "2000-01-01", null);
        ActorDTO actor = actorService.createActor(actorDTO);
        ResponseEntity<List<ActorDTO>> responseEntity = actorController.getActorsByFilter("!@#", null, null, true);
        List<ActorDTO> foundActor = responseEntity.getBody();

        assertNotNull(foundActor);
        assertTrue(foundActor.contains(actor));
    }

    @Test
    @Transactional
    void testFilterActorPagination() throws BadRequestException {
        ActorDTO actorDTO = new ActorDTO(null, "Actor Test!@#", "2000-01-01", null);
        actorService.createActor(actorDTO);
        ActorDTO actorDTO1 = new ActorDTO(null, "Actor Test!@#", "2000-01-01", null);
        actorService.createActor(actorDTO1);
        ResponseEntity<List<ActorDTO>> responseEntity = actorController.getActorsByFilter("!@#", 0, 1, true);
        List<ActorDTO> foundActor = responseEntity.getBody();

        assertNotNull(foundActor);
        assertEquals(1, foundActor.size());
        responseEntity = actorController.getActorsByFilter("!@#", 1, 1, true);
        foundActor = responseEntity.getBody();

        assertNotNull(foundActor);
        assertEquals(1, foundActor.size());
    }

    @Test
    @Transactional
    void testGetActorMovies() throws ResourceNotFoundException, BadRequestException {
        MovieDTO movie = new MovieDTO(1L, "Movie", 1982, null, null, null);
        movieRepository.save(new Movie(movie));
        List<MovieDTO> movieList = new ArrayList<>();
        movieList.add(movie);
        ActorDTO actorDTO = new ActorDTO(null, "Actor Test", "2000-01-01", movieList);
        ActorDTO actor = actorService.createActor(actorDTO);
        // Assume that actor has movies associated
        ResponseEntity<List<MovieDTO>> responseEntity = actorController.getActorMovies(actor.getId());
        List<MovieDTO> movies = responseEntity.getBody();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    @Transactional
    void testUpdateActor() throws ResourceNotFoundException, BadRequestException {
        ActorDTO actor = actorService.createActor(new ActorDTO(null, "Actor Update", "2000-01-01", null));
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Updated Actor");

        ResponseEntity<ActorDTO> responseEntity = actorController.updateActor(actor.getId(), updates, false);
        ActorDTO updatedActor = responseEntity.getBody();

        assertNotNull(updatedActor);
        assertEquals("Updated Actor", updatedActor.getName());
    }

    @Test
    @Transactional
    void testDeleteActor() throws ResourceNotFoundException, BadRequestException {
        ActorDTO actorDTO = new ActorDTO(null, "Actor Test", "2000-01-01", null);
        ActorDTO actor = actorService.createActor(actorDTO);

        // Delete the actor
        actorController.deleteActor(actor.getId(), true);

        // Verify that the actor has been deleted
        assertThrows(ResourceNotFoundException.class, () -> actorController.getActorById(actor.getId(), true));
    }
}