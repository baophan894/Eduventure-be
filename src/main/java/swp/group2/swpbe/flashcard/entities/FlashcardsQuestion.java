package swp.group2.swpbe.flashcard.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "flashcards_question")
public class FlashcardsQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String question;
    private String answer;
    // @Column(name = "questionUrl") 
    // private String questionUrl;

    // @Column(name = "answerUrl") 
    // private String answerUrl;

    @ManyToOne
    @JoinColumn(name = "flashcards_id")
    @JsonBackReference

    private Flashcard flashcards;

    public FlashcardsQuestion(Flashcard flashcards, String question,String answer) {
        this.flashcards = flashcards;
        this.question = question;
        
       // this.questionUrl = questionUrl;
        this.answer = answer;
        //this.answerUrl = answerUrl;
    }

    public FlashcardsQuestion() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
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

    public Flashcard getFlashcards() {
        return this.flashcards;
    }

    public void setFlashcards(Flashcard flashcards) {
        this.flashcards = flashcards;
    }

}