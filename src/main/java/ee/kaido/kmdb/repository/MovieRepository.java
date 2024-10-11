package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.model.Actor;
import ee.kaido.kmdb.model.Genre;
import ee.kaido.kmdb.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, PagingAndSortingRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m " +
            "WHERE (:title IS NULL OR LOWER(m.title) LIKE %:title%) " +
            "AND (:genre IS NULL OR :genre MEMBER OF m.genres) " +
            "AND (:releaseYear IS NULL OR m.releasedYear = :releaseYear) " +
            "AND (:actor IS NULL OR :actor MEMBER OF m.actors)")
    List<Movie> getMoviesByFilters(@Param("genre") Genre genreId, @Param("releaseYear") Integer releaseYear, @Param("actor") Actor actorId, @Param("title") String title);
    @Query("SELECT m FROM Movie m " +
            "WHERE (:title IS NULL OR LOWER(m.title) LIKE %:title%) " +
            "AND (:genre IS NULL OR :genre MEMBER OF m.genres) " +
            "AND (:releaseYear IS NULL OR m.releasedYear = :releaseYear) " +
            "AND (:actor IS NULL OR :actor MEMBER OF m.actors)")
    List<Movie> getMoviesByFiltersPageable(@Param("genre") Genre genreId, @Param("releaseYear") Integer releaseYear, @Param("actor") Actor actorId, @Param("title") String title,Pageable pageable);
}
