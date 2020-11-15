package telran.movies.service.api;

import java.util.List;


import telran.movies.dto.*;


public interface MoviesService {
void addMovie(MovieDto movieDto);
MovieDto getMovie(String name);
List<MovieDto> getMoviesProducer(String producer);
List<MovieDto> getMoviesYears(int yearFrom, int yearTo);
MovieDto deleteMovie(String name);
List<MovieDto> getMovies();
void addCinema(CinemaDto cinemaDto);
void addWatch(WatchDto watchDto);

List<MovieDto> moviesWatchesGreaterThan(int nWatches);
List<MovieDto> mostPopularMovies(int amount);
List<CinemaDto> profitableCinemas();

List<MovieDto> profitableMoviesCity(String city);

List<CinemaDto> cinemasLeastMovieWatchesProducer(int nCinemas, String producer);
MovieDto updateProducer(String movieName, String producer);

List<MovieCinemaTickets> getMoviesCinemasTickets();
List<CinemaProfit>getCinemasProfits() ;
List<CinemaDto> deleteCinemasLessProfit(long profit);
CinemaDto updatePlaces(String cinemaName, int places);
CinemaDto getCinema(String cinemaName);
List<CinemaDto> getCinemas();
List<CityCount> getCitiesWatchesProducer(String producer);
int watchesCount();
}
