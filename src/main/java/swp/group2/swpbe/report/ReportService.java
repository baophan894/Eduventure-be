package swp.group2.swpbe.report;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.constant.ResourceType;
import swp.group2.swpbe.course.CourseRepository;
import swp.group2.swpbe.course.entites.Course;
import swp.group2.swpbe.document.DocumentRepository;
import swp.group2.swpbe.document.entities.Document;
import swp.group2.swpbe.flashcard.FlashcardRepository;
import swp.group2.swpbe.flashcard.entities.Flashcard;
import swp.group2.swpbe.post.PostRepository;
import swp.group2.swpbe.post.entities.Post;
import swp.group2.swpbe.report.entities.Report;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    ReportRepository reportRepository;
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CourseRepository courseRepository;

    public List<Report> getAllReportsSortedByDate() {
        return reportRepository.findAllByOrderByReportDateDesc();
    }

    public void uploadReport(String userId, ResourceType resourceType, String resourceId, String reason) {

        Report report = new Report(userId, resourceType, resourceId, reason);
        reportRepository.save(report);
    }

    public void handleReport(int reportId) {
        Report report = reportRepository.findById(reportId);
        int resourceId = Integer.parseInt(report.getResourceId());
        switch (report.getResourceType()) {
            case document: {
                Document document = documentRepository.findById(resourceId);
                if (document != null) {
                    document.setState(ResourceStatus.pending);
                    documentRepository.save(document);
                }

                break;
            }
            case flashcard: {
                Flashcard flashcard = flashcardRepository.findById(resourceId);
                if (flashcard != null) {
                    flashcard.setState(ResourceStatus.pending);
                    flashcardRepository.save(flashcard);
                }
                break;
            }
            case course: {
                Course course = courseRepository.findById(resourceId);
                if (course != null) {
                    course.setState(ResourceStatus.pending);
                    courseRepository.save(course);
                }
                break;
            }
            case post: {
                Post post = postRepository.findById(resourceId);
                if (post != null) {
                    postRepository.delete(post);
                }
                break;
            }
            default:
                break;
        }

        reportRepository.delete(report);
    }
}
