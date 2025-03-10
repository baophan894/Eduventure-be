package swp.group2.swpbe.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swp.group2.swpbe.CloudinaryService;
import swp.group2.swpbe.constant.PaymentStatus;
import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.course.dto.ReviewCourseDTO;
import swp.group2.swpbe.course.entites.Course;
import swp.group2.swpbe.course.entites.CourseOrder;
import swp.group2.swpbe.course.entites.CourseReview;
import swp.group2.swpbe.course.entites.Lesson;
import swp.group2.swpbe.course.response.StudentResponse;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.user.UserRepository;
import swp.group2.swpbe.user.entities.User;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    LessonRepository lessonRepository;
    @Autowired
    CourseOrderRepository courseOrderRepository;
    @Autowired
    CourseReviewRepository courseReviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CloudinaryService cloudinaryService;

    public Course createCourse(String expert_id, Double price, String description, String name, String banner_url,
            String topic_id) {
        Course course = new Course(expert_id, price, description, banner_url, name, topic_id, ResourceStatus.pending);
        return courseRepository.save(course);

    }

    public Course updateCourse(int courseId, String expertId, Double price, String description, String name,
            String topicId, String bannerUrl) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new ApiRequestException("Course not found", HttpStatus.BAD_REQUEST);
        }
        if (bannerUrl != null) {
            course.setBannerUrl(bannerUrl);
        }
        course.setExpertId(expertId);
        course.setPrice(price);
        course.setDescription(description);
        course.setName(name);
        course.setTopicId(topicId);
        course.setUpdatedAt(new Date());

        return courseRepository.save(course);

    }

    public Course updateCourseBanner(int courseId, String bannerUrl) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new ApiRequestException("Course not found", HttpStatus.BAD_REQUEST);
        }
        course.setBannerUrl(bannerUrl);
        course.setUpdatedAt(new Date());
        return courseRepository.save(course);

    }

    public void createLesson(Course course, String videoUrl, int order, String name) {
        Lesson lesson = new Lesson(videoUrl, order, name);
        lesson.setCourse(course);
        lessonRepository.save(lesson);
    }

    public Lesson updateLesson(int lessonId, String videoUrl, int order, String name) {
        Lesson lesson = lessonRepository.findById(lessonId);
        lesson.setName(name);
        lesson.setVideoUrl(videoUrl);
        lesson.setLessonOrder(order);
        return lessonRepository.save(lesson);
    }

    public List<Course> getExpertCourse(String expertId) {
        return courseRepository.findByExpertId(expertId);
    }

    public List<Course> getAllCourse() {
        return courseRepository.findAll();
    }

    public List<Course> getAllActiveCourse() {
        List<Course> courses = courseRepository.findByState(ResourceStatus.active);
        Collections.sort(courses, new Comparator<Course>() {
            @Override
            public int compare(Course o1, Course o2) {
                double totalRating1 = o1.getReviews().stream()
                        .mapToDouble(CourseReview::getRate)
                        .sum();
                double totalRating2 = o2.getReviews().stream()
                        .mapToDouble(CourseReview::getRate)
                        .sum();
                return Double.compare(totalRating2, totalRating1);
            }
        });
        return courses;
    }

    public Course acceptCourse(int id) {
        Course course = courseRepository.findById(id);
        course.setState(ResourceStatus.active);
        return courseRepository.save(course);
    }

    public List<CourseOrder> getUserOrder(String userId) {
        return courseOrderRepository.findByUserIdAndPaymentStatus(Integer.parseInt(userId), PaymentStatus.paid);
    }

    public Course getCourseById(int courseId) {
        Course course = courseRepository.findById(courseId);
        if (course == null) {
            throw new ApiRequestException("Course not found", HttpStatus.NOT_FOUND);
        }
        return course;
    }

    public List<CourseOrder> getPaidOrders() {
        return courseOrderRepository.findByPaymentStatus(PaymentStatus.paid);
    }

    public void createReview(ReviewCourseDTO body, int userId) {
        int courseId = body.getCourseId();
        Course course = courseRepository.findById(courseId);
        CourseReview reviewExist = courseReviewRepository.findByUserIdAndCourse(userId, course);
        if (reviewExist != null) {
            throw new ApiRequestException("You can't review 2 times", HttpStatus.BAD_REQUEST);
        }
        String content = body.getContent();
        int rate = body.getRate();
        CourseReview review = new CourseReview(userId, course, content, rate);
        courseReviewRepository.save(review);
    }

    public List<StudentResponse> getCourseStudent(int courseId, String expertId) {
        Course course = courseRepository.findByIdAndExpertId(courseId,
                expertId);
        if (course == null) {
            throw new ApiRequestException("Course not found", HttpStatus.BAD_REQUEST);
        }
        List<CourseOrder> orderList = courseOrderRepository.findByCourseIdAndPaymentStatus(courseId,
                PaymentStatus.paid);
        List<StudentResponse> studentList = new ArrayList<>();
        for (CourseOrder order : orderList) {
            int userId = order.getUserId();
            User user = userRepository.findById(userId);
            StudentResponse student = new StudentResponse(user.getFullName(), user.getAvatarUrl(), user.getId(),
                    order.getCreatedAt());
            studentList.add(student);
        }
        return studentList;
    }

    public void addCourseLesson(int courseId, MultipartFile[] files, String expertId) {
        Course course = courseRepository.findByIdAndExpertId(courseId, expertId);
        if (course == null) {
            throw new ApiRequestException("Course not found", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getOriginalFilename();
            String lesson = fileName.substring(0, fileName.length() - 4);
            String[] lessonHandleArr = lesson.split("-");
            Map lessonData = this.cloudinaryService.upload(files[i]);
            String lessonUrl = (String) lessonData.get("url");
            this.createLesson(course, lessonUrl, Integer.parseInt(lessonHandleArr[0].trim()),
                    lessonHandleArr[1]);
        }
    }

    public void updateCourseState(Course course, ResourceStatus state) {
        course.setState(state);
        courseRepository.save(course);
    }

    public List<Course> getAllPendingCourse() {
        return courseRepository.findByState(ResourceStatus.pending);
    }

}
