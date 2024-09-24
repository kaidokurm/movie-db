package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
