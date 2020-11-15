package telran.movies.dto;

import javax.validation.constraints.*;

public class CinemaDto {

private static final int MIN_PLACES = 0;
private static final int MAX_PLACES = 0;

@NotEmpty
public String name;
 @NotEmpty
public String city;
@Min(MIN_PLACES) @Max(MAX_PLACES)
public int places;
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
	CinemaDto other = (CinemaDto) obj;
	if (name == null) {
		if (other.name != null)
			return false;
	} else if (!name.equals(other.name))
		return false;
	return true;
}
}
