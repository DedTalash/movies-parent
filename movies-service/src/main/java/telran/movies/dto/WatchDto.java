package telran.movies.dto;

import javax.validation.constraints.*;

public class WatchDto {
	@NotNull @NotEmpty
public String movieName;
	@NotNull @NotEmpty
public String cinemaName;
	@NotNull @NotEmpty
public String dateTime;
	@Min(10) @Max(500) 
public int ticketCost;
	@Positive
public int tickets;
}
