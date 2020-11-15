package telran;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.client.result.UpdateResult;

public class MongoAdditionalOperationsImpl implements MongoAdditionalOperations {
@Autowired
MongoTemplate mongoTemplate;
	@Override
	public UpdateResult decreaseMarksMissingSubject(String subject, int delta) {
		//Query query = new BasicQuery(String.format("{marks:{$not:{$elemMatch:{subject:'%s'}}}}", subject));
		Query query = new Query(Criteria.where("marks")
				.not()
				.elemMatch(Criteria.where("subject").is(subject)));
		Update update = new Update();
		//{$inc:{'marks.$[].mark':-20}}) 
		update.inc("marks.$[].mark", -delta);
		return mongoTemplate.updateMulti(query, update , Student.class);
	}
	@Override
	@Transactional
	public List<String> getBestStudents(int nStudents) {
		UnwindOperation unwindOperation = Aggregation.unwind("marks");
		GroupOperation groupOperation = Aggregation.group("name").avg("marks.mark").as("avgMark");
		SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.DESC, "avgMark"));
		LimitOperation limit = Aggregation.limit(nStudents);
		Aggregation pipeline = Aggregation.newAggregation(unwindOperation, groupOperation,
				sortOperation, limit);
		AggregationResults<Document> result =  mongoTemplate.aggregate(pipeline,Student.class, Document.class);
		
		
		return result.getMappedResults().stream().map(d -> d.getString("_id")).collect(Collectors.toList());
	}
	@Override
	public double getAverageMark() {
		UnwindOperation unwindOperation = Aggregation.unwind("marks");
		GroupOperation groupOperation = Aggregation.group().avg("marks.mark").as("avgMark");
		Aggregation pipeline = Aggregation.newAggregation(unwindOperation, groupOperation);
		return mongoTemplate.aggregate(pipeline,Student.class, Document.class).getUniqueMappedResult().getDouble("avgMark");
	}
	@Override
	@Transactional
	public List<String> getStudentsAvgMarkGreaterTotalAvg() {
		double avgMark =  getAverageMark();
		UnwindOperation unwindOperation = Aggregation.unwind("marks");
		GroupOperation groupOperation = Aggregation.group("name").avg("marks.mark").as("avgMark");
		MatchOperation matchOperation = Aggregation.match(Criteria.where("avgMark").gte(avgMark));
		Aggregation pipeline = Aggregation.newAggregation(unwindOperation, groupOperation, matchOperation);
AggregationResults<Document> result =  mongoTemplate.aggregate(pipeline,Student.class, Document.class);
		
		
		return result.getMappedResults().stream().map(d -> d.getString("_id")).collect(Collectors.toList());
	}

}
