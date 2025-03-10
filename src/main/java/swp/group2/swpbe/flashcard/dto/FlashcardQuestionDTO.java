package swp.group2.swpbe.flashcard.dto;

public class FlashcardQuestionDTO {
    private String question;
    private String answer;
    // private String questionUrl;
    // private String answerUrl;

    // Constructors
    public FlashcardQuestionDTO() {
    }

    public FlashcardQuestionDTO(String question, String answer) {
        this.question = question;
        
        //this.questionUrl = questionUrl;
        this.answer = answer;
        // this.answerUrl = answerUrl;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    // public String getQuestionUrl() {
    //     return this.questionUrl;
    // }

    // public void setQuestionUrl(String questionUrl) {
    //     this.questionUrl = questionUrl;
    // }

    // public String getAnswerUrl() {
    //     return this.answerUrl;
    // }

    // public void setAnswerUrl(String answerUrl) {
    //     this.answerUrl = answerUrl;
    // }
}