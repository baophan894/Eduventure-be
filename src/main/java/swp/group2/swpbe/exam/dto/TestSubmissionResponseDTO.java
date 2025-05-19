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
    private Integer timeSpent; // Time spent in seconds

    // Test information
    private Integer testTypeId;
    private String testTypeName;
    private String testTitle;
    private String instructorName;

    // Statistics
    private Integer totalCorrectAnswers;
    private Integer totalQuestions;

    // Grouped answers by part
    private List<TestPartSubmissionDTO> partSubmissions;

    // Flag to control whether to include detailed answers
    private boolean includeDetailedAnswers = false;
}