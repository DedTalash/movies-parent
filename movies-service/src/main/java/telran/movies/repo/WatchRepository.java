package telran.movies.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.movies.dto.CinemaProfit;
import telran.movies.dto.CityCount;
import telran.movies.dto.MovieCinemaTickets;
import telran.movies.entities.Watch;

public interface WatchRepository extends JpaRepository<Watch, Long> {
//Query method naming
	List<Watch> findByMovieName(String name);
@Query(value="select name as cinema, coalesce(sum(ticket_cost * tickets),0)"
		+ " as profit from cinemas left join watches on name=cinema_name group by name order by"
		+ " coalesce(sum(ticket_cost * tickets),0)", nativeQuery = true)
	List<CinemaProfit> findCinemasProfits();
/********************************************************************************/
//JPQL
@Query("select c.name as cinema, coalesce(sum(ticketCost * tickets),0) as profit from Watch w right join w.cinema c"
		+ " where c.name=:cinema group by c.name ")
CinemaProfit findCinemaProfit(@Param("cinema")String cinema);
/************************************************************************/
@Query(value="select m.name as movie, c.name as cinema, coalesce(sum(tickets),0) as tickets "
		+ "from movies m left join watches on m.name=movie_name full outer join cinemas c on c.name=cinema_name "
		+ "group by m.name, c.name order by coalesce(sum(tickets),0) ", nativeQuery=true)
List<MovieCinemaTickets> findMoviesCinemasTickets();
/***********************************************************************/
@Query(value="select city, count(tickets) as count from movies m left join watches on m.name=movie_name "
		+ "full outer join cinemas c on c.name=cinema_name where producer=:producer "
		+ "group by city order by count(tickets) ", nativeQuery = true)
List<CityCount> findCitiesWatchesProducer(@Param("producer")String producer);



}
