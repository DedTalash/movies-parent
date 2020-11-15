package telran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@SpringBootApplication
public class MongoTestAppl {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(MongoTestAppl.class, args);
		StudentsRepository students = ctx.getBean(StudentsRepository.class);
	
		
		
		
		
//		System.out.println(students.findByName("Sara"));
		//students.deleteByName("Moshe");
//		System.out.println("******************************************\nAll students:\n");
//		System.out.println(students.findAll());
		//System.out.println(students.decreaseMarksMissingSubject("Java Technologies", 20));
		//System.out.println(students.findAll());
		System.out.println(students.getBestStudents(2));
		System.out.println("Average mark of all students is " + students.getAverageMark());
		System.out.println(students.getStudentsAvgMarkGreaterTotalAvg());
		//System.out.println(students.findBySubjectAndOthers(new String[] {"Java"},85, 85));
		
		ctx.close();
		
		

	}

}
