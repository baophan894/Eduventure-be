package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestPartSubmissionDTO {
    private Integer partId;
    private String partName;
    private String partDescription;
    private Integer partDuration;
    private String partIcon;

    // Part-specific statistics
    private Integer correctAnswers;
    private Integer totalQuestions;
    private Double partScore;

    private List<SubmittedAnswerResponseDTO> answers;
}