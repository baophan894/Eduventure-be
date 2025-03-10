package swp.group2.swpbe.flashcard.entities;

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

@Entity(name = "flashcards")
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Column(name = "user_id")
    private String userId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "topic_id")
    private String topicId;
    private String description;
    @OneToMany(mappedBy = "flashcards")
    @JsonManagedReference
    private List<FlashcardsQuestion> questions;
    @Enumerated(EnumType.STRING)
    private ResourceStatus state;

    @OneToMany(mappedBy = "flashcards")
    @JsonManagedReference
    private List<FlashcardReview> reviews;

    public Flashcard(String name, String userId, String topicId, String description) {
        this.name = name;
        this.userId = userId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.topicId = topicId;
        this.description = description;
        this.state = ResourceStatus.pending;
 
    }

    public Flashcard() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FlashcardsQuestion> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<FlashcardsQuestion> questions) {
        this.questions = questions;
    }

    public List<FlashcardReview> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<FlashcardReview> reviews) {
        this.reviews = reviews;
    }

    public ResourceStatus getState() {
        return this.state;
    }

    public void setState(ResourceStatus state) {
        this.state = state;
    }

}