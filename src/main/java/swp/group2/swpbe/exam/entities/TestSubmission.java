package swp.group2.swpbe.exam.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "test_submissions")
public class TestSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmittedAnswer> submittedAnswers = new ArrayList<>();

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSubmissionPart> submissionParts = new ArrayList<>();

    // Helper method for managing submitted answers
    public void addSubmittedAnswer(SubmittedAnswer answer) {
        submittedAnswers.add(answer);
        answer.setSubmission(this);
    }

    public void removeSubmittedAnswer(SubmittedAnswer answer) {
        submittedAnswers.remove(answer);
        answer.setSubmission(null);
    }

    public void addSubmissionPart(TestSubmissionPart part) {
        submissionParts.add(part);
        part.setSubmission(this);
    }
}