package telran.movies.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import telran.movies.dto.*;
import telran.movies.service.api.MoviesService;

@RestController
@Validated
@Valid
public class MoviesRestController {
	private static final int N_PRODUCERS = 10;
	private static final int MIN_YEAR = 2000;
	private static final int MAX_YEAR = 2020;
	private static final int N_CITIES = 5;
	private static final int MIN_PLACES = 100;
	private static final int MAX_PLACES = 1000;
	private static final int MIN_TICKET_COST = 20;
	private static final int MAX_TICKET_COST = 200;
	@Value("${telran.generation.movies.count:0}")
	int nMovies;
	@Value("${telran.generation.watches.count:0}")
	int nWatches;
	@Value("${telran.generation.cinemas.count:0}")
	int nCinemas;
	static Logger LOG = LoggerFactory.getLogger(MoviesRestController.class);
@Autowired 
MoviesService service;
@PostConstruct //annotates method getting control once an application context is created
void creatingDB() {
	if (service.watchesCount() == 0 && nWatches > 0) {
		List<WatchDto> watches = createRandomData();
		LOG.info("DB with {} watches has been created", watches.size());
	}
}
@PreDestroy
void contextWillClosed() {
	LOG.info("Context closed, bye!");
}
@PostMapping("movies")
MovieDto addMovie(@RequestBody @Valid MovieDto movie) {
	service.addMovie(movie);
	LOG.debug("movie {} added", movie.name);
	return movie;
}
@PostMapping("movies/cinema")
CinemaDto addCinema(@RequestBody @Valid CinemaDto cinema) {
	service.addCinema(cinema);
	LOG.debug("cinema {} added", cinema.name);
	return cinema;
}
@PostMapping("movies/watch")
WatchDto addWatch(@RequestBody @Valid WatchDto watch) {
	service.addWatch(watch);
	LOG.debug("watch of movie {} in cinema {} added", watch.movieName, watch.cinemaName);
	return watch;
}

List<WatchDto> createRandomData ()  {
	ArrayList<MovieDto> movies = getRandomMovies(nMovies);
	ArrayList<CinemaDto> cinemas = getRandomCinemas(nCinemas);
	List<WatchDto> watches = getRandomWatches(movies, cinemas, nWatches);
	createDB(movies, cinemas, watches);
	return watches;
}
private void createDB(ArrayList<MovieDto> movies, ArrayList<CinemaDto> cinemas, List<WatchDto> watches) {
	movies.forEach(this::addMovie);
	cinemas.forEach(this::addCinema);
	watches.forEach(this::addWatch);
	
}
private List<WatchDto> getRandomWatches(ArrayList<MovieDto> movies, ArrayList<CinemaDto> cinemas, int nWatches) {
	
	return Stream.generate(()->getRandomWatch(movies, cinemas)).limit(nWatches).collect(Collectors.toList());
}
private ArrayList<CinemaDto> getRandomCinemas(int nCinemas) {
	
 return Stream.generate(this::getRandomCinema).distinct().limit(nCinemas)
			.collect(Collectors.toCollection(ArrayList::new));
}
private MovieDto getRandomMovie() {
	String name = "movie" + getRandomNumber(1, Integer.MAX_VALUE);
	String producer = "producer" + getRandomNumber(1, N_PRODUCERS + 1);
	
	int year = getRandomNumber(MIN_YEAR, MAX_YEAR + 1);
	return new MovieDto(name , producer, year );
}
private CinemaDto getRandomCinema () {
	CinemaDto res = new CinemaDto();
	res.city = "city" + getRandomNumber(1, N_CITIES + 1);
	res.name = "cinema" + getRandomNumber(1, Integer.MAX_VALUE);
	res.places = getRandomNumber(MIN_PLACES, MAX_PLACES);
	return res;
}
private WatchDto getRandomWatch(ArrayList<MovieDto> movies, ArrayList<CinemaDto> cinemas) {
	WatchDto res = new WatchDto();
	CinemaDto cinema = cinemas.get(getRandomNumber(0, cinemas.size()));
	res.cinemaName = cinema.name;
	res.dateTime = LocalDateTime.of(2020, getRandomNumber(1,12), getRandomNumber(1,28), getRandomNumber(10,21), 0)
			.format(DateTimeFormatter.ISO_DATE_TIME);
	res.movieName = movies.get(getRandomNumber(0, movies.size())).name;
	res.ticketCost = getRandomNumber(MIN_TICKET_COST, MAX_TICKET_COST);
	res.tickets = getRandomNumber(1, cinema.places);
	return res;
}
private int getRandomNumber(int minValue, int maxValue) {
	
	return (int) (minValue + Math.random() * (maxValue - minValue));
}
private ArrayList<MovieDto> getRandomMovies(int nMovies) {

	
	return Stream.generate(this::getRandomMovie).distinct().limit(nMovies)
			.collect(Collectors.toCollection(ArrayList::new));
}
@GetMapping("movies/cinemas/profitable") 
List<CinemaDto> getProfitableCinemas() {
	return service.profitableCinemas();
}
@GetMapping("movies/producer")
List<MovieDto> getMoviesProducer(@RequestParam (name = "producer")@NotNull String producer) {
	return service.getMoviesProducer(producer);
}
@GetMapping("movies/years")
List<MovieDto> getMoviesYears(@RequestParam(name="from", defaultValue = "0")int yearFrom,
		@RequestParam(name="to", defaultValue = "3000")int yearTo) {
	return service.getMoviesYears(yearFrom, yearTo);
}
@GetMapping("movies/cinemas/watches/producer/least") 
List<CinemaDto> getCinemasPorducerLeast(@RequestParam(name="producer")@NotNull @NotEmpty String producer,
		@RequestParam(name="amount", defaultValue = "5")int nCinemas){
	return service.cinemasLeastMovieWatchesProducer(nCinemas, producer);
}
@GetMapping("movies/city/profitable")
List<MovieDto> getMoviesCityProfitable(@RequestParam (name="city") @NotNull String city) {
	return service.profitableMoviesCity(city);
}
@DeleteMapping("movies/cinemas/profit") 
List<CinemaDto> deleteNotProfitableCinemas(@RequestParam(name="profit", defaultValue = "100") long profit) {
	return service.deleteCinemasLessProfit(profit);
}
@DeleteMapping("movies")
MovieDto deleteMovie(@RequestParam(name="name") String movieName) {
	MovieDto movie =  service.deleteMovie(movieName);
	LOG.debug("movie {} has been deleted ", movieName);
	return movie;
}
@PutMapping("movies/{name}")
MovieDto updateProducer(@RequestBody String producer, @PathVariable(name = "name")
String movieName) {
	MovieDto movie = service.updateProducer(movieName, producer);
	LOG.debug("new producer {} has been set in movie {}",producer, movieName );
	return movie;
}
@PutMapping("movies/cinemas/{cinema}")
CinemaDto updatePlaces(@RequestBody int places, @PathVariable(name="cinema") String cinemaName) {
	return service.updatePlaces(cinemaName, places);
}
@GetMapping("movies/cinemas/profits")
List<CinemaProfit> getCinemasProfits() {
	return service.getCinemasProfits();
}
@GetMapping("movies/cinemas/tickets")
List<MovieCinemaTickets> getMoviesCinemasTickets(){
	return service.getMoviesCinemasTickets();
}
@GetMapping("movies/cities/watches") 
List<CityCount> getCitiesWatchesProducer(@RequestParam(name="producer", required = true)  @NotEmpty String producer) {
	LOG.debug("producer {}", producer);
	return service.getCitiesWatchesProducer(producer);
}
}
