package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Integer id;
    private Integer typeId;
    private String typeName;
    private String title;
    private String questionInstruction;
    private String answerInstruction;
    private String imageUrl;
    private String readingPassage;
    private String correctAnswer;
    private Integer order;
    private List<QuestionOptionDTO> questionOptions;
}