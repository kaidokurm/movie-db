package ee.kaido.kmdb.model;

import com.fasterxml.jackson.annotation.JsonTypeId;

@Entity
public class Actor {
    @Id
    private Long id;
    private String name;
    private Date birthDate;
    @ManyToMany
    private Movie movie;
}
