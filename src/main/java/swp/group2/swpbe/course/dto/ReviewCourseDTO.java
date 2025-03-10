package swp.group2.swpbe.course.dto;

public class ReviewCourseDTO {
    private int rate;
    private int courseId;
    private String content;

    public ReviewCourseDTO() {
    }

    public ReviewCourseDTO(int rate, int courseId, String content) {
        this.rate = rate;
        this.courseId = courseId;
        this.content = content;
    }

    public int getRate() {
        return this.rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getCourseId() {
        return this.courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
