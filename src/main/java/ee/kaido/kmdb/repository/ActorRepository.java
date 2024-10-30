package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.entity.Actor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query("SELECT a FROM Actor a  " +
            "WHERE (:name IS NULL OR LOWER( a.name) LIKE %:name%)")
    List<Actor> findByNameContains(@Param("name") String name);

    @Query("SELECT a FROM Actor a  " +
            "WHERE (:name IS NULL OR LOWER( a.name) LIKE %:name%)")
    List<Actor> findByNameContainsPageable(@Param("name") String name, Pageable pageable);
}
