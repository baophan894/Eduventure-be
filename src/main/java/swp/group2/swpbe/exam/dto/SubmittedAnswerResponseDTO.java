package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmittedAnswerResponseDTO {
    private Integer id;
    private Integer questionId;
    private String writtenAnswer;
    private Boolean isCorrect;
    private List<String> selectedOptionIds;
}