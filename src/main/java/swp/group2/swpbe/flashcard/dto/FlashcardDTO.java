package swp.group2.swpbe.flashcard.dto;

import java.util.List;

public class FlashcardDTO {
    private String name;
    private String description;
    private String topicId;
    private List<FlashcardQuestionDTO> questions;

    public FlashcardDTO() {
    }

    public FlashcardDTO(String name, String description, String topicId, List<FlashcardQuestionDTO> questions) {
        this.name = name;
        this.description = description;
        this.topicId = topicId;
        this.questions = questions;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopicId() {
        return this.topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public List<FlashcardQuestionDTO> getQuestions() {
        return this.questions;
    }

    public void setQuestions(List<FlashcardQuestionDTO> questions) {
        this.questions = questions;
    }

}