package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class MovieDTO {
    private Long id;
    @NotBlank(message = "Title is required")
    @Size(min = 2, message = "The title should have at least 2 characters")
    private String title;

    private int releasedYear;

    private Duration duration;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Genre> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActorDTO> actors;

    public MovieDTO(Movie movie, boolean actorVisible) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.releasedYear = movie.getReleasedYear();
        this.duration = movie.getDuration();
        this.genres = movie.getGenres() == null ? null : new ArrayList<>(movie.getGenres());
        if (actorVisible && movie.getActors() != null) {
            List<ActorDTO> actors = new ArrayList<>();
            for (Actor actor : movie.getActors()) {
                actors.add(new ActorDTO(actor, false));
            }
            this.actors = actors;
        }
    }
}
