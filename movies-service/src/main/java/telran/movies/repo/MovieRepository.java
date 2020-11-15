package telran.movies.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.movies.entities.Movie;

public interface MovieRepository extends JpaRepository<Movie, String> {

	@Query(value = "select * from movies where producer=:producer", nativeQuery = true)
	List<Movie> findByProducer(@Param("producer") String producer);
	/*********************************************/
@Query(value="select * from movies where year between :from and :to", nativeQuery=true)
	List<Movie> findByYearBetween(@Param("from") int yearFrom, @Param("to")int yearTo);
/******************************************************/
@Query(value="select * from movies where name in (select movie_name from  watches "
		+ "group by movie_name having count(*) > :nWatches )", nativeQuery = true)
List<Movie> findWatchesGreaterThan(@Param("nWatches")int nWatches);
/*****************************************************/
@Query (value="select * from movies where name in (select movie_name from watches "
		+ "group by movie_name order by sum(tickets) desc limit :limit",nativeQuery = true)
List<Movie> findMostPopularMovies(@Param("limit")int amount);
/********************************************************/
@Query(value="select * from movies where name in"
		+ " (select movie_name from cinemas join watches  on "
		+ "name=cinema_name  "
		+ "where city=:city grop by movie_name having avg(ticket_cost * tickets) > "
		+ "(select avg(ticket_cost * tickets) from cinemas join watches  on name=cinema_name "
		+ "where city=:city) )", nativeQuery = true)
List<Movie> findByCityProfitable(@Param("city")String city);



}
