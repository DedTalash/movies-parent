package telran.movies.dto;

import javax.validation.constraints.*;

public class MovieDto {
	@NotNull @NotEmpty
public String name;
	@NotNull @NotEmpty
public String producer;
	@Min(1940) @Max(2020)
public int year;
public MovieDto() {
}
public MovieDto(String name, String producer, int year) {
	super();
	this.name = name;
	this.producer = producer;
	this.year = year;
}
@Override
public String toString() {
	return "MovieDto [name=" + name + ", producer=" + producer + ", year=" + year + "]";
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	MovieDto other = (MovieDto) obj;
	if (name == null) {
		if (other.name != null)
			return false;
	} else if (!name.equals(other.name))
		return false;
	return true;
}

}
