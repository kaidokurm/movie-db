package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByNameContains(String name);
}
