package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByGenreName(String genreName);

    List<Movie> findByReleaseYear(int year);
}
