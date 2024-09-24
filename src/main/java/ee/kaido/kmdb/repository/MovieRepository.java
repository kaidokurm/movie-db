package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
