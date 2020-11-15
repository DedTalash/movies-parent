package telran;

import java.util.Arrays;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "students")
public class Student {
public int _id;
public String name;
public SubjectMark[] marks;
@Override
public String toString() {
	return "Student [_id=" + _id + ", name=" + name + ", marks=" + Arrays.toString(marks) + "]";
}
}
