package telran.movies.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import telran.movies.repo.*;
import telran.movies.dto.*;

import telran.movies.entities.*;
import telran.movies.exceptions.InvalidInputException;
import telran.movies.exceptions.NotFoundException;
import telran.movies.service.api.MoviesService;
@Service
@Transactional (readOnly = true)
public class MoviesServiceJpaImpl implements MoviesService {
@Autowired 
CinemaRepository cinemaRepo;
@Autowired 
MovieRepository movieRepo;
@Autowired 
WatchRepository watchRepo;
	@Override
	@Transactional
	public void addMovie(MovieDto movieDto) {
	
		if (movieRepo.existsById(movieDto.name)) {
			throw new InvalidInputException("Movie already exists " + movieDto.name); 
		}
		Movie movie = toMovie(movieDto);
		movieRepo.save(movie);

	}

	private Movie toMovie(MovieDto movieDto) {
		
		return new Movie(movieDto.name, movieDto.producer, movieDto.year);
	}

	@Override
	public MovieDto getMovie(String name) {
		Movie movie = movieRepo.findById(name).orElse(null);
		if (movie==null) {
			throw new NotFoundException(String.format("movie %s doesn't exist", name));
		}
		return toMovieDto(movie);
	}

	@Override
	public List<MovieDto> getMoviesProducer(String producer) {
		
		return toListMovieDto(movieRepo.findByProducer(producer));
	}

	private List<MovieDto> toListMovieDto(List<Movie> movies) {
		
		return movies.stream().map(this::toMovieDto).collect(Collectors.toList());
	}
	private MovieDto toMovieDto(Movie movie) {
		MovieDto res = new MovieDto();
		res.name = movie.getName();
		res.producer = movie.getProducer();
		res.year = movie.getYear();
		return res;
	}

	@Override
	public List<MovieDto> getMoviesYears(int yearFrom, int yearTo) {
		
		return toListMovieDto(movieRepo.findByYearBetween(yearFrom, yearTo));
	}

	@Override
	@Transactional
	public MovieDto deleteMovie(String name) {
		Movie movie = movieRepo.findById(name).orElse(null);
		if (movie==null) {
			throw new NotFoundException(String.format("Movie %s doesn't exist", name));
		}
		List<Watch> watches = watchRepo.findByMovieName(name);
		watches.forEach(w->w.setMovie(null));
		movieRepo.delete(movie);
		return toMovieDto(movie);
		

	}

	@Override
	public List<MovieDto> getMovies() {
		
		return toListMovieDto(movieRepo.findAll());
	}

	@Override
	@Transactional
	public void addCinema(CinemaDto cinemaDto) {
		if(cinemaRepo.existsById(cinemaDto.name)) {
			throw new InvalidInputException("Cinema already exists " + cinemaDto.name);
		}
		Cinema cinema = toCinema(cinemaDto);
		cinemaRepo.save(cinema);

	}

	private Cinema toCinema(CinemaDto cinemaDto) {
		
		return new Cinema(cinemaDto.name, cinemaDto.city, cinemaDto.places);
	}

	@Override
	@Transactional
	public void addWatch(WatchDto watchDto) {
	Cinema cinema = cinemaRepo.findById(watchDto.cinemaName).orElse(null);
	if (cinema == null) {
		throw new NotFoundException("no cinema " + watchDto.cinemaName);
	}
	if(cinema.getPlaces() < watchDto.tickets) {
		throw new InvalidInputException("number of tickets can't be greater than number of laces in cinema");
	}
	Movie movie = movieRepo.findById(watchDto.movieName).orElse(null);
	if (movie == null) {
		throw new NotFoundException("no movie " + watchDto.movieName);
	}
	LocalDateTime dateTime = LocalDateTime.parse(watchDto.dateTime);
	
	Watch watch = new Watch(dateTime, watchDto.ticketCost, watchDto.tickets, cinema, movie);
	watchRepo.save(watch);

	}

	@Override
	public List<MovieDto> moviesWatchesGreaterThan(int nWatches) {
		
		return toListMovieDto(movieRepo.findWatchesGreaterThan(nWatches));
	}

	@Override
	public List<MovieDto> mostPopularMovies(int amount) {
		
		return toListMovieDto(movieRepo.findMostPopularMovies(amount));
	}

	@Override
	public List<CinemaDto> profitableCinemas() {
		//get cinemas with profit for first third of all cinemas
		return toListCinemaDto(cinemaRepo.findProfitableCinemas());
	}

	private List<CinemaDto> toListCinemaDto(List<Cinema> cinemas) {
		
		return cinemas.stream().map(this::toCinemaDto).collect(Collectors.toList());
	}
private CinemaDto toCinemaDto(Cinema cinema) {
	CinemaDto res = new CinemaDto();
	res.city = cinema.getCity();
	res.name = cinema.getName();
	res.places = cinema.getPlaces();
	return res;
}
	@Override
	public List<MovieDto> profitableMoviesCity(String city) {
		//  get all movies with  profit greater than average profit for all movies in the given city
		
		return toListMovieDto(movieRepo.findByCityProfitable(city));
	}

	@Override
	public List<CinemaDto> cinemasLeastMovieWatchesProducer(int nCinemas, String producer) {
		// get the given number of cinemas of the given producer with least number of watches
		return toListCinemaDto(cinemaRepo.findByProducerLeastMovieWatches(nCinemas, producer));
	}

	@Override
	@Transactional
	public MovieDto updateProducer(String movieName, String producer) {
		Movie movie = movieRepo.findById(movieName).orElse(null);
		if (movie == null) {
			throw new NotFoundException(String.format("Movie %s doesn't exist", movieName));
		}
		movie.setProducer(producer);
		return toMovieDto(movie);

	}

	@Override
	public List<MovieCinemaTickets> getMoviesCinemasTickets() {
		
		return watchRepo.findMoviesCinemasTickets();
	}

	@Override
	public List<CinemaProfit> getCinemasProfits() {
		
		return watchRepo.findCinemasProfits();
	}

	@Override
	@Transactional
	public List<CinemaDto> deleteCinemasLessProfit(long profit) {
		List<Cinema> cinemas = cinemaRepo.findByProfitLess(profit) ;
		
		cinemas.forEach(cinemaRepo::delete);
		return toListCinemaDto(cinemas);

	}

	@Override
	@Transactional
	public CinemaDto updatePlaces(String cinemaName, int places) {
		Cinema cinema = cinemaRepo.findById(cinemaName).orElse(null);
		if (cinema == null) {
			throw new NotFoundException(String.format("Cinema %s doesn't exist",cinemaName));
			
		}
		cinema.setPlaces(places);
		return toCinemaDto(cinema);

	}

	@Override
	public CinemaDto getCinema(String cinemaName) {
		Cinema cinema = cinemaRepo.findById(cinemaName).orElse(null);
		if (cinema==null) {
			throw new NotFoundException(String.format("cinema %s doesn't exist", cinemaName));
		}
		return toCinemaDto(cinema);
	}

	@Override
	public List<CinemaDto> getCinemas() {
		
		return toListCinemaDto(cinemaRepo.findAll());
	}

	@Override
	public List<CityCount> getCitiesWatchesProducer(String producer) {
		
		return watchRepo.findCitiesWatchesProducer(producer);
	}

	@Override
	public int watchesCount() {
		
		return (int) watchRepo.count();
	}

}
