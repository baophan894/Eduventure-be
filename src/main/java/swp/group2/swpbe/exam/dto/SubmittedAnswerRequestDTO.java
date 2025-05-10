package swp.group2.swpbe.exam.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SubmittedAnswerRequestDTO {
    @NotNull(message = "Question ID is required")
    private Integer questionId;

    private String writtenAnswer;

    @Valid
    private List<SelectedOptionDTO> selectedOptions;
}