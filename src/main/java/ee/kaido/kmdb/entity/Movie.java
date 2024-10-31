package ee.kaido.kmdb.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ee.kaido.kmdb.deserializers.ActorListDeserializer;
import ee.kaido.kmdb.deserializers.DurationDeserializer;
import ee.kaido.kmdb.deserializers.GenreListDeserializer;
import ee.kaido.kmdb.dto.MovieDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private int releaseYear;
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;
    @JsonDeserialize(using = GenreListDeserializer.class)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    @JoinTable(name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
    @JsonDeserialize(using = ActorListDeserializer.class)
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH,
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
                    CascadeType.REFRESH})
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id", referencedColumnName = "id")
    )
    private List<Actor> actors = new ArrayList<>();


    public Movie(MovieDTO movieDto, List<Actor> actorList) {
        this.setTitle(movieDto.getTitle());
        this.setReleaseYear(movieDto.getReleaseYear());
        this.setDuration(movieDto.getDuration());
        this.setGenres(movieDto.getGenres());
        this.setActors(actorList);
    }

    public Movie(MovieDTO movieDto) {
        this.setTitle(movieDto.getTitle());
        this.setReleaseYear(movieDto.getReleaseYear());
        this.setDuration(movieDto.getDuration());
        this.setGenres(movieDto.getGenres());
    }

    public void removeActor(Actor actor) {
        this.getActors().remove(actor);
    }

    public void removeGenre(Genre genre) {
        this.getGenres().remove(genre);
    }
}
