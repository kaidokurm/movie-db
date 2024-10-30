package ee.kaido.kmdb.repository;

import ee.kaido.kmdb.entity.Actor;
import ee.kaido.kmdb.entity.Genre;
import ee.kaido.kmdb.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, PagingAndSortingRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m " +
            "WHERE (:title IS NULL OR LOWER(m.title) LIKE %:title%) " +
            "AND (:genre IS NULL OR :genre MEMBER OF m.genres) " +
            "AND (:releaseYear IS NULL OR m.releaseYear = :releaseYear) " +
            "AND (:actor IS NULL OR :actor MEMBER OF m.actors)")
    List<Movie> getMoviesByFilters(@Param("genre") Genre genreId, @Param("releaseYear") Integer releaseYear, @Param("actor") Actor actorId, @Param("title") String title);

    @Query("SELECT m FROM Movie m " +
            "WHERE (:title IS NULL OR LOWER(m.title) LIKE %:title%) " +
            "AND (:genre IS NULL OR :genre MEMBER OF m.genres) " +
            "AND (:releaseYear IS NULL OR m.releaseYear = :releaseYear) " +
            "AND (:actor IS NULL OR :actor MEMBER OF m.actors)")
    List<Movie> getMoviesByFiltersPageable(@Param("genre") Genre genreId, @Param("releaseYear") Integer releaseYear, @Param("actor") Actor actorId, @Param("title") String title, Pageable pageable);
}