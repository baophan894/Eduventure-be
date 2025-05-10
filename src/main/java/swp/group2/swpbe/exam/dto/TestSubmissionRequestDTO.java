package swp.group2.swpbe.exam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestSubmissionRequestDTO {
    @NotNull(message = "Test ID is required")
    private Integer testId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Submission time is required")
    private LocalDateTime submittedAt;

    private List<Integer> partIds; // Optional list of test part IDs to take

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<SubmittedAnswerRequestDTO> submittedAnswers;
}