package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private TestPart part;

    @ManyToOne
    @JoinColumn(name = "type", nullable = false)
    private QuestionType type;

    @Column(name = "title")
    private String title;

    @Column(name = "question_instruction")
    private String questionInstruction;

    @Column(name = "answer_instruction")
    private String answerInstruction;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "reading_passage")
    private String readingPassage;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "question_order", nullable = false)
    private Integer order;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> questionOptions;
}