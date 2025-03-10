package swp.group2.swpbe.course;

import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.course.entites.Course;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {
     Course findById(int id);

     List<Course> findByExpertId(String expertId);

     List<Course> findByState(ResourceStatus state);

     Course findByIdAndExpertId(int courseId, String expertId);

}
