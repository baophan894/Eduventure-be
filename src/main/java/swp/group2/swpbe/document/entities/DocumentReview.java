package swp.group2.swpbe.document.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import swp.group2.swpbe.constant.ReviewState;

@Entity(name = "document_review")
public class DocumentReview {
    @Id
    private int id;
    @Enumerated(EnumType.STRING)
    private ReviewState state;
    @Column(name = "user_id")
    private String userId;
    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonBackReference
    private Document document;

    public DocumentReview() {
    }

    public DocumentReview(ReviewState state, String userId, Document document) {
        this.state = state;
        this.userId = userId;
        this.document = document;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

   
    public ReviewState getState() {
        return this.state;
    }

    public void setState(ReviewState state) {
        this.state = state;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Document getDocument() {
        return this.document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
