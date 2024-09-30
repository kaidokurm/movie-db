package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
    List<Movie> findMoviesByGenreId(@Param("genreId") Long genreId);
}
