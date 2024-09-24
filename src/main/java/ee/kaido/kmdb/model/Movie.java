package ee.kaido.kmdb.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private int releasedYear;
    private Date duration;
    @ManyToMany
    private Actor actor;
}
