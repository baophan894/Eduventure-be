package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "test_submission_parts")
public class TestSubmissionPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private TestSubmission submission;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private TestPart part;
}