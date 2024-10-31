package ee.kaido.kmdb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ee.kaido.kmdb.deserializers.ActorListDeserializer;
import ee.kaido.kmdb.deserializers.DurationDeserializer;
import ee.kaido.kmdb.deserializers.GenreListDeserializer;
import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import jakarta.validation.constraints.Min;
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
    @Size(min = 1, message = "The title should have at least 1 characters")
    private String title;
    @Min(value = 1800, message = "Minimum year is 1800")
    private int releaseYear;
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    @JsonDeserialize(using = GenreListDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Genre> genres;

    @JsonDeserialize(using = ActorListDeserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ActorDTO> actors;


    //Constructor
    public MovieDTO(Movie movie, boolean hideActors) {
        if (movie.getId() != null)
            this.id = movie.getId();
        this.title = movie.getTitle();
        this.releaseYear = movie.getReleaseYear();
        this.duration = movie.getDuration();
        this.genres = movie.getGenres() == null ? null : movie.getGenres();
        if (!hideActors && movie.getActors() != null) {
            List<ActorDTO> actors = new ArrayList<>();
            for (Actor actor : movie.getActors()) {
                actors.add(new ActorDTO(actor, true));
            }
            this.actors = actors;
        }
    }
}
