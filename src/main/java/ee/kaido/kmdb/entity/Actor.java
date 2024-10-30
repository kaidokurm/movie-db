package ee.kaido.kmdb.entity;


import ee.kaido.kmdb.dto.ActorDTO;
import ee.kaido.kmdb.exception.BadRequestException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import static ee.kaido.kmdb.deserializers.checkers.Checks.wordFirstLetterToHigh;

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

    public Actor(ActorDTO actorDTO) throws BadRequestException {
        this.id = actorDTO.getId();
        this.name = actorDTO.getName();
        setBirthDate(actorDTO.getBirthDate());
    }

    public void setName(@Size(min = 1, message = "Minimum name length is 1 character") @NotNull(message = "Name is required") String name) {
        this.name = wordFirstLetterToHigh(name);
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
