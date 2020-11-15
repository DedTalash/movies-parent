package telran.movies.entities;
import java.time.LocalDateTime;

import javax.persistence.*;
@Entity
@Table(name="watches",indexes = {@Index(columnList = "cinema_name"), @Index(columnList = "movie_name")})
public class Watch {
	@Id
	@GeneratedValue
	@Column(name = "watch_id" )
	long watchId;
	@Column(name="date_time")
LocalDateTime dateTime;
	@Column(name="ticket_cost")
int ticketCost;
int tickets;
@ManyToOne
@JoinColumn(name="cinema_name")
Cinema cinema;
@ManyToOne
@JoinColumn(name="movie_name")
Movie movie;
public void setMovie(Movie movie) {
	this.movie = movie;
}
public Watch() {
}
public Watch(LocalDateTime dateTime, int ticketCost, int tickets, Cinema cinema, Movie movie) {
	super();
	this.dateTime = dateTime;
	this.ticketCost = ticketCost;
	this.tickets = tickets;
	this.cinema = cinema;
	this.movie = movie;
}
public long getWatchId() {
	return watchId;
}
public LocalDateTime getDateTime() {
	return dateTime;
}
public int getTicketCost() {
	return ticketCost;
}
public int getTickets() {
	return tickets;
}
public Cinema getCinema() {
	return cinema;
}
public Movie getMovie() {
	return movie;
}
@Override
public String toString() {
	return "Watch [watchId=" + watchId + ", dateTime=" + dateTime + ", ticketCost=" + ticketCost + ", tickets="
			+ tickets + ", cinema=" + cinema + ", movie=" + movie + "]";
}

}
