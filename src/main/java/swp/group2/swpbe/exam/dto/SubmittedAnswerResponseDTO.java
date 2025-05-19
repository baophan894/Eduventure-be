package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmittedAnswerResponseDTO {
    private Integer id;
    private Integer questionId;
    private String questionTitle;
    private String questionInstruction;
    private String answerInstruction;
    private String audioUrl;
    private String imageUrl;
    private String readingPassage;
    private String questionType;
    private String writtenAnswer;
    private Boolean isCorrect;
    private List<String> selectedOptionIds;
    private String correctAnswer;
    private Integer order;
    private List<QuestionOptionDTO> questionOptions;
}