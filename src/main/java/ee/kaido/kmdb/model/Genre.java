package ee.kaido.kmdb.model;
@Entity
public class Genre {
    @Id
    private Long id;
    private String name;
    @OneToMany
    private Movie movie;
}
