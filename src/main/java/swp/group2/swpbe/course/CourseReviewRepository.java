package swp.group2.swpbe.course;

import org.springframework.data.jpa.repository.JpaRepository;

import swp.group2.swpbe.course.entites.Course;
import swp.group2.swpbe.course.entites.CourseReview;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Integer> {
    CourseReview findByUserIdAndCourse(int userId, Course course);
} 
