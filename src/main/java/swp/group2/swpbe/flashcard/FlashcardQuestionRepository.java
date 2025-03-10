package swp.group2.swpbe.flashcard;

import org.springframework.data.jpa.repository.JpaRepository;
import swp.group2.swpbe.flashcard.entities.Flashcard;
import swp.group2.swpbe.flashcard.entities.FlashcardsQuestion;

public interface FlashcardQuestionRepository extends JpaRepository<FlashcardsQuestion, Integer> {
    void deleteByFlashcards(Flashcard flashcard);
}