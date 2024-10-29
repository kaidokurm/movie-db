package ee.kaido.kmdb.model;


import ee.kaido.kmdb.controller.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    private List<Movie> movies;

    public Actor(ActorDTO actorDTO) throws ParseException {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        this.birthDate = formatter.parse(actorDTO.getBirthDate());
//        this.movies = actorDTO.getMovies().stream().map(movieDTO -> new Movie(movieDTO)).collect(Collectors.toList());
    }

    public void setName(String name) {
        if (name.isBlank() || name.trim().isEmpty())
            throw new IllegalArgumentException("Actor name cannot be blank or empty");
        this.name = name;
    }

    public void setBirthDate(String date) throws BadRequestException {
        if (!date.isBlank() || !date.trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
            try {
                LocalDate localDate = LocalDate.parse(date, formatter);
                this.birthDate = Date.from(localDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
            } catch (DateTimeParseException e) {
                throw new BadRequestException(date + " is not a valid date format 'yyyy-MM-dd'");
            }
        }
    }
}
