package telran.logging.repo;

import java.util.List;
import telran.logging.dto.ExceptionCount;
public interface MongoAdditionalOperations {

List<ExceptionCount> findMostEncounteredExceptions(int nStudents);
long getSecurityExceptionsCount();
}
