package swp.group2.swpbe.document.entities;

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
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "file_url")
    private String fileUrl;
    @Column(name = "created_at")
    private Date createdAt;
    private String description;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "topic_id")
    private String topicId;
    private String title;
    @Enumerated(EnumType.STRING)
    private ResourceStatus state;

    @OneToMany(mappedBy = "document")
    @JsonManagedReference
    private List<DocumentReview> reviews;

    public Document(String fileUrl, String title, String description, String userId, String topicId) {
        this.fileUrl = fileUrl;
        this.createdAt = new Date();
        this.description = description;
        this.userId = userId;
        this.topicId = topicId;
        this.title = title;
        this.state = ResourceStatus.pending;
    }

    public Document() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileUrl() {
        return this.fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DocumentReview> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<DocumentReview> reviews) {
        this.reviews = reviews;
    }

    public ResourceStatus getState() {
        return this.state;
    }

    public void setState(ResourceStatus state) {
        this.state = state;
    }

}
