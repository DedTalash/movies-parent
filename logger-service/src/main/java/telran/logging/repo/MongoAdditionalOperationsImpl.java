package telran.logging.repo;

import org.bson.Document;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import telran.logging.api.LoggingManagementMongoImpl;
import telran.logging.documents.Logger;
import telran.logging.dto.ExceptionCount;
import telran.logging.dto.ExceptionType;

import org.springframework.data.mongodb.core.query.Query;
import java.util.List;
import java.util.stream.Collectors;

public class MongoAdditionalOperationsImpl implements MongoAdditionalOperations{
    static org.slf4j.Logger LOG = LoggerFactory.getLogger(LoggingManagementMongoImpl.class);
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<ExceptionCount> findMostEncounteredExceptions(int nExceptions) {
        GroupOperation groupOperation = Aggregation.group("exceptionType").count().as("count");
        LimitOperation limit = Aggregation.limit(nExceptions);
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC,"count"));
        Aggregation pipeline = Aggregation.newAggregation(groupOperation, sortOperation,limit);
        AggregationResults<ExceptionCount> result =  mongoTemplate.aggregate(pipeline, Logger.class, ExceptionCount.class);
        LOG.debug("{}",result);
       return result.getMappedResults();
    }

    @Override
    public long getSecurityExceptionsCount() {
      MatchOperation matchOperation = Aggregation.match(Criteria.where("exceptionType").in("AUTHENTICATION_EXCEPTION","AUTHORIZATION_EXCEPTION"));
        GroupOperation groupOperation = Aggregation.group("exceptionType").count().as("count");
        Aggregation pipeline = Aggregation.newAggregation(matchOperation, groupOperation);
        AggregationResults<Document> result =  mongoTemplate.aggregate(pipeline, Logger.class, Document.class);
        return result.getMappedResults().stream().map(d -> d.getInteger("count"))
                .reduce(0,(a, b) -> a + b);
    }


}
