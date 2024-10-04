package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByName(String name);

    @Query("SELECT g FROM Genre g WHERE (:name IS NULL OR LOWER(g.name) LIKE %:name%)")
    List<Genre> findAllByName(@Param("name") String name);
}
