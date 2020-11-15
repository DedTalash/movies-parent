package telran.logging.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import telran.logging.documents.Logger;
import telran.logging.dto.ExceptionType;

import java.time.LocalDateTime;
import java.util.List;

public interface LoggerRepository extends MongoRepository<Logger, ObjectId>, MongoAdditionalOperations {


//    @Query("{$and:[{dateTime:{$gte:?0,$lte:?1}},{isException:true}]}")
    List<Logger> findByDateTimeBetweenAndIsException(LocalDateTime from, LocalDateTime to,
    		boolean isException);

    List<Logger> findByDateTimeBetween(LocalDateTime from, LocalDateTime to);
    //@Query("{$and:[{dateTime:{$gte:?0,$lte:?1}},{className:?2}]}")
    List<Logger> findByDateTimeBetweenAndClassName(LocalDateTime from, LocalDateTime to,String className );

    //@Query("{$and:[{dateTime:{$gte:?0,$lte:?1}},{exceptionType:?2}]}")
    List<Logger> findByDateTimeBetweenAndExceptionType(LocalDateTime from, LocalDateTime to, ExceptionType type);

    @Query("{$and:[{dateTime:{$gte:?0, $lte:?1}},{className:?2},{methodName:?3}]}")
    List<Logger> findLogsClassMethod(LocalDateTime from, LocalDateTime to,
    		String className, String methodName);

}
