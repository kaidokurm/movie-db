package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import ee.kaido.kmdb.controller.exception.ResourceNotFoundException;
import ee.kaido.kmdb.service.ActorService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// for actor returning
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActorDTO {

    private Long id;
    @Size(min = 1, message = "Minimum name length is 1 character")
    @NotNull(message = "Name is required")
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in the format yyyy-MM-dd")
    private String birthDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MovieDTO> movies;


    public ActorDTO(Actor actor, boolean showMovies) {
        if (actor.getId() != null)
            this.id = actor.getId();
        this.name = actor.getName();

        // Check if actor's birthdate is not null before formatting
        Date birthDate = actor.getBirthDate();
        if (birthDate != null) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            this.birthDate = formatter.format(birthDate);
        } else {
            this.birthDate = null; // Or handle it as you see fit
        }

        if (showMovies && actor.getMovies() != null) {
            List<MovieDTO> movieDTOS = new ArrayList<>();
            for (Movie movie : actor.getMovies()) {
                movieDTOS.add(new MovieDTO(movie, false));
            }
            this.movies = movieDTOS;
        }
    }

    public ActorDTO(Long id, ActorService actorService) throws ResourceNotFoundException {
        Actor actor =
                actorService.getActorByIdOrThrowError(id);
        this.id = id;
        this.name = actor.getName();
        this.birthDate = String.valueOf(actor.getBirthDate());
        this.movies = new ArrayList<>();
        for (Movie movie : actor.getMovies()) {
            this.movies.add(new MovieDTO(movie, true));
        }
    }
}
