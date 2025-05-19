package swp.group2.swpbe.exam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestSubmissionRequestDTO {
    @NotNull(message = "Test ID is required")
    private Integer testId;

    private Long userId; // Optional - will be set from token if authenticated

    @NotNull(message = "Submission time is required")
    private LocalDateTime submittedAt;

    @NotNull(message = "Time spent is required")
    private Integer timeSpent; // Time spent in seconds

    private List<Integer> partIds; // Optional list of test part IDs to take

    @Valid
    private List<SubmittedAnswerRequestDTO> submittedAnswers;
}