package swp.group2.swpbe.expert.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import swp.group2.swpbe.constant.ExpertRequestStatus;

import java.util.Date;

@Entity
@Table(name = "expert_request")
public class ExpertRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "cv_url")
    private String cvUrl;
    @Column(name = "created_at")
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private ExpertRequestStatus state;

    public ExpertRequest(int id, String userId, String cvUrl, Date createdAt, ExpertRequestStatus state) {
        this.id = id;
        this.userId = userId;
        this.cvUrl = cvUrl;
        this.createdAt = createdAt;
        this.state = state;
    }

    public ExpertRequest() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCvUrl() {
        return this.cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public ExpertRequestStatus getState() {
        return this.state;
    }

    public void setState(ExpertRequestStatus state) {
        this.state = state;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
