package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query("SELECT a FROM Actor a  WHERE LOWER( a.name) LIKE %:name%")
    List<Actor> findByNameContains(@Param("name") String name);
}
