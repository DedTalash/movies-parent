package telran;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface StudentsRepository extends MongoRepository<Student, Integer>, MongoAdditionalOperations{
//@Query("{name:?0}")
	Student findByName(String name);
//@DeleteQuery("{name:?0}")
	void deleteByName(String name);
@Query("{$and:[{marks:{$elemMatch:{subject:{$in: ?0},mark:{$gte:?1}}}}"
		+ ",{marks:{$not:{$elemMatch:{subject: {$nin:?0},mark:{$gte:?2}}}}}]}") 
List<Student> findBySubjectAndOthers(String[] subjects, int subjectMark, int othersMark);

}
