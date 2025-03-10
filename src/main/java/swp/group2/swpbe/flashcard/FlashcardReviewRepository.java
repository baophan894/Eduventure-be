package swp.group2.swpbe.flashcard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.flashcard.entities.Flashcard;
import swp.group2.swpbe.flashcard.entities.FlashcardReview;
@Repository
public interface FlashcardReviewRepository extends JpaRepository<FlashcardReview, Integer> {
    FlashcardReview findByFlashcardsAndUserId(Flashcard flashcard, String userId );
    long countByState(@Param("state") ReviewState state);


}
