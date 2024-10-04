package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ActorDTO {
    private Long id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MovieDTO> movies;

    public ActorDTO(Actor actor, boolean showMovies) {
        this.id = actor.getId();
        this.name = actor.getName();
        this.birthDate = actor.getBirthDate();
        if (showMovies && actor.getMovies() != null) {
            List<MovieDTO> movieDTOS = new ArrayList<>();
            for (Movie movie : actor.getMovies()) {
                movieDTOS.add(new MovieDTO(movie, false));
            }
            this.movies = movieDTOS;
        }
    }
}
