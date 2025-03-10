package swp.group2.swpbe.statistics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swp.group2.swpbe.constant.PaymentStatus;
import swp.group2.swpbe.course.CourseService;
import swp.group2.swpbe.course.entites.Course;
import swp.group2.swpbe.course.entites.CourseOrder;
import swp.group2.swpbe.document.DocumentService;
import swp.group2.swpbe.flashcard.FlashcardService;
import swp.group2.swpbe.user.UserService;

@Service
public class StatisticService {
    @Autowired
    UserService userService;
    @Autowired
    DocumentService documentService;
    @Autowired
    CourseService courseService;
    @Autowired
    FlashcardService flashcardService;

    public StatisticResponse getAdminStatistic() {
        StatisticResponse statisticResponse = new StatisticResponse();
        int totalUser = userService.getAllUser().size();
        int user = userService.getAllUserRole().size();
        int expert = userService.getAllExpert().size();
        int totalDocument = documentService.getAllDocuments().size();
        int totalFlashcard = flashcardService.getAllFlashCard().size();
        double totalIncome = calculateTotalIncome();

        statisticResponse.setTotalUser(totalUser);
        statisticResponse.setUsers(user);
        statisticResponse.setExpert(expert);
        statisticResponse.setTotalDocument(totalDocument);
        statisticResponse.setTotalFlashcard(totalFlashcard);
        statisticResponse.setTotalIncome(totalIncome);
        return statisticResponse;
    }

    private double calculateTotalIncome() {
        double totalIncome = 0.0;
        List<CourseOrder> paidOrders = courseService.getPaidOrders();
        for (CourseOrder order : paidOrders) {
            int courseId = order.getCourse().getId();
            PaymentStatus paymentStatus = order.getPaymentStatus();
            Course course = courseService.getCourseById(courseId);

            if (course != null && PaymentStatus.paid.equals(paymentStatus)) {
                totalIncome += course.getPrice() * 0.05;
            }
        }

        return totalIncome;
    }
}
