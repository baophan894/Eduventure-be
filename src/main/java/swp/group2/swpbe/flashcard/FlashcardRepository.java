package swp.group2.swpbe.flashcard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swp.group2.swpbe.flashcard.entities.Flashcard;
@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {
    Flashcard findById(int id);
    List<Flashcard> findByUserId(String userId);
    Flashcard findByIdAndUserId(int id, String userId);
} 