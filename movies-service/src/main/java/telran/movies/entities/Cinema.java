package telran.movies.entities;

import java.util.List;

import javax.persistence.*;
@Entity
@Table(name="cinemas")
public class Cinema {
	@Id
String name;
String city;
int places;
public void setPlaces(int places) {
	this.places = places;
}
@OneToMany(mappedBy = "cinema", cascade = CascadeType.REMOVE)
List<Watch> watches;

public Cinema() {
	
}
public Cinema(String name, String city, int places) {
	super();
	this.name = name;
	this.city = city;
	this.places = places;
}
public String getName() {
	return name;
}
public String getCity() {
	return city;
}
public int getPlaces() {
	return places;
}
@Override
public String toString() {
	return "Cinema [name=" + name + ", city=" + city + ", places=" + places + "]";
}

}
