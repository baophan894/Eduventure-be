package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Integer id;
    private Integer typeId;
    private String title;
    private String questionInstruction;
    private String answerInstruction;
    private String audioUrl;
    private String imageUrl;
    private String readingPassage;
    private String correctAnswer;
    private List<QuestionOptionDTO> questionOptions;
}