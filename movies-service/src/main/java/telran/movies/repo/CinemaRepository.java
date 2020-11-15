package telran.movies.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import telran.movies.entities.Cinema;

public interface CinemaRepository extends JpaRepository<Cinema, String>{

	@Query(value="select * from  cinemas where name in (select cinema_name from "
			+ " watches  group by cinema_name  having sum(ticket_cost * tickets) > "
			+ "(select avg(profit) from (select cinema_name, sum(ticket_cost * tickets) as profit "
			+ "from watches group by cinema_name ) as profits )) ", nativeQuery = true)
	List<Cinema> findProfitableCinemas();
	/*********************************************************/
@Query(value="select * from cinemas where name in"
		+ " (select cinema_name from watches w right join movies m"
		+ " on movie_name=m.name where producer=:producer group by cinema_name "
		+ "order by count(tickets) limit :limit"
		+ ")", nativeQuery=true)
	List<Cinema> findByProducerLeastMovieWatches(@Param("limit")int nCinemas, String producer);
/********************************************************************/
@Query(value="select * from cinemas where name in (select cinema_name from watches "
		+ "group by cinema_name having sum(ticket_cost * tickets) < :profit)", nativeQuery=true)
	List<Cinema> findByProfitLess(@Param("profit") long profit);

}
