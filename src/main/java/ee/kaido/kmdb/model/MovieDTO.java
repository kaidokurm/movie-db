package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ee.kaido.kmdb.deserializers.ActorListDeserializer;
import ee.kaido.kmdb.deserializers.DurationDeserializer;
import ee.kaido.kmdb.deserializers.GenreListDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 2, message = "The title should have at least 2 characters")
    private String title;

    private int releasedYear;
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    @JsonDeserialize(using = GenreListDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Genre> genres;

    @JsonDeserialize(using = ActorListDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActorDTO> actors;

    public MovieDTO(Movie movie, boolean actorVisible) {
        if (movie.getId() != null)
            this.id = movie.getId();
        this.title = movie.getTitle();
        this.releasedYear = movie.getReleasedYear();
        this.duration = movie.getDuration();
        this.genres = movie.getGenres() == null ? null : movie.getGenres();
        if (actorVisible && movie.getActors() != null) {
            List<ActorDTO> actors = new ArrayList<>();
            for (Actor actor : movie.getActors()) {
                actors.add(new ActorDTO(actor, false));
            }
            this.actors = actors;
        }
    }
}
