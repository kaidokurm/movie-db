package ee.kaido.kmdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.util.Date;

@Entity
public class Actor {
    @Id
    private Long id;
    private String name;
    private Date birthDate;
    @ManyToMany
    private Movie movie;
}
