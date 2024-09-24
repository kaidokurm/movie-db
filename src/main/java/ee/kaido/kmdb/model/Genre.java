package ee.kaido.kmdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Genre {
    @Id
    private Long id;
    private String name;
    @OneToMany
    private Movie movie;
}
