package ee.kaido.kmdb.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import ee.kaido.kmdb.controller.exception.BadRequestException;
import jakarta.persistence.*;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY)
    private List<Movie> movies;

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
