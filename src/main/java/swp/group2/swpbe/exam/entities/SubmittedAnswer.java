package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submitted_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private TestSubmission submission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "written_answer", columnDefinition = "TEXT")
    private String writtenAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @ElementCollection
    @CollectionTable(name = "submitted_answer_options", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "option_id")
    private List<String> selectedOptionIds = new ArrayList<>();
}