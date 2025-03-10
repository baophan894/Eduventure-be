package swp.group2.swpbe.course.entites;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import swp.group2.swpbe.constant.ResourceStatus;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "expert_id")
    private String expertId;
    private Double price;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    private String description;
    @Column(name = "banner_url")
    private String bannerUrl;
    private String name;
    @OneToMany(mappedBy = "course")
    @JsonManagedReference
    private List<Lesson> lessons;
    @OneToMany(mappedBy = "course")
    @JsonManagedReference
    private List<CourseOrder> orders;
    @OneToMany(mappedBy = "course")
    @JsonManagedReference
    private List<CourseReview> reviews;
    @Column(name = "topic_id")
    private String topicId;
    @Enumerated(EnumType.STRING)
    private ResourceStatus state;

    public Course() {
    }

    public Course(String expertId, Double price, String description, String bannerUrl, String name, String topicId,
            ResourceStatus state) {
        this.expertId = expertId;
        this.price = price;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.description = description;
        this.bannerUrl = bannerUrl;
        this.name = name;
        this.topicId = topicId;
        this.state = state;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpertId() {
        return this.expertId;
    }

    public void setExpertId(String expertId) {
        this.expertId = expertId;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBannerUrl() {
        return this.bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Lesson> getLessons() {
        return this.lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<CourseOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(List<CourseOrder> orders) {
        this.orders = orders;
    }

    public List<CourseReview> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<CourseReview> reviews) {
        this.reviews = reviews;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public ResourceStatus getState() {
        return this.state;
    }

    public void setState(ResourceStatus state) {
        this.state = state;
    }

}
