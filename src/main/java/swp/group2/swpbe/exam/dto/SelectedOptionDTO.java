package swp.group2.swpbe.exam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SelectedOptionDTO {
    @NotBlank(message = "Option ID is required")
    private String optionId;
}