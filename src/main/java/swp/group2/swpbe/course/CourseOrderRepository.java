package swp.group2.swpbe.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import swp.group2.swpbe.constant.PaymentStatus;
import swp.group2.swpbe.course.entites.CourseOrder;

public interface CourseOrderRepository extends JpaRepository<CourseOrder, Integer> {
    List<CourseOrder> findByCourseIdAndPaymentStatus(int courseId, PaymentStatus paymentStatus);

    List<CourseOrder> findByUserIdAndPaymentStatus(int userId, PaymentStatus paymentStatus);

    CourseOrder findByCourseIdAndUserId(int courseId, int userId);

    List<CourseOrder> findByPaymentStatus(PaymentStatus paymentStatus);

    List<CourseOrder> findByCourseId(int courseId);

}
