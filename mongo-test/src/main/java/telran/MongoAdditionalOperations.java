package telran;

import java.util.List;

import com.mongodb.client.result.UpdateResult;

public interface MongoAdditionalOperations {
UpdateResult decreaseMarksMissingSubject(String subject, int delta);
List<String> getBestStudents(int nStudents);
double getAverageMark();
List<String> getStudentsAvgMarkGreaterTotalAvg();
}
