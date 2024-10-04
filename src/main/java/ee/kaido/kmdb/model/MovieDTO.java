package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MovieDTO {
    private Long id;
    @NotBlank(message = "Title is required")
    @Size(min = 2, message = "The title should have at least 2 characters")
    private String title;

    private int releasedYear;

    private Duration duration;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActorDTO> actors;


    public MovieDTO(Long id, String title, int releasedYear, Duration duration) {
        this.id = id;
        this.title = title;
        this.releasedYear = releasedYear;
        this.duration = duration;
    }

    public MovieDTO(Long id, String title, int releasedYear, Duration duration, List<ActorDTO> actors) {
        this.id = id;
        this.title = title;
        this.releasedYear = releasedYear;
        this.duration = duration;
        this.actors = actors;
    }

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.releasedYear = movie.getReleasedYear();
        this.duration = movie.getDuration();
        List<ActorDTO> actors = new ArrayList<>();
        for (Actor actor : movie.getActors()) {
            actors.add(new ActorDTO(actor, false));
        }
    }
}
