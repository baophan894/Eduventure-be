package swp.group2.swpbe.course;

import swp.group2.swpbe.course.entites.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    Lesson findById(int id);
}