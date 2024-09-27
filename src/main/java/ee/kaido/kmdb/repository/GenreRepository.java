package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByName(String name);
}
