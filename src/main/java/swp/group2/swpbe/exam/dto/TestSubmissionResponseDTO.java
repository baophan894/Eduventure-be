package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestSubmissionResponseDTO {
    private Integer id;
    private Integer testId;
    private Long userId;
    private LocalDateTime submittedAt;
    private String status;

    // Statistics
    private Integer totalCorrectAnswers;
    private Integer totalQuestions;

    // Grouped answers by part
    private List<TestPartSubmissionDTO> partSubmissions;
}