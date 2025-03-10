package swp.group2.swpbe.flashcard;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.flashcard.dto.FlashcardDTO;
import swp.group2.swpbe.flashcard.dto.FlashcardQuestionDTO;
import swp.group2.swpbe.flashcard.entities.Flashcard;
import swp.group2.swpbe.flashcard.entities.FlashcardReview;
import swp.group2.swpbe.flashcard.entities.FlashcardsQuestion;

@Service
public class FlashcardService {
    @Autowired
    FlashcardRepository flashcardRepository;
    @Autowired
    FlashcardQuestionRepository flashcardQuestionRepository;
    @Autowired
    FlashcardReviewRepository flashcardReviewRepository;

    public Flashcard create(FlashcardDTO body, String userId) {
        String name = body.getName();
        String description = body.getDescription();
        String topicId = body.getTopicId();
        Flashcard flashcard = new Flashcard(name, userId, topicId, description);
        List<FlashcardQuestionDTO> questionList = body.getQuestions();
        Flashcard newFlashcard = flashcardRepository.save(flashcard);
        for (int i = 0; i < questionList.size(); i++) {
            FlashcardsQuestion question = new FlashcardsQuestion(newFlashcard, questionList.get(i).getQuestion(),
                    questionList.get(i).getAnswer());
            flashcardQuestionRepository.save(question);
        }
        return newFlashcard;
    }

    @Transactional
    public Flashcard update(FlashcardDTO body, String flashcardId, String userId) {

        String name = body.getName();
        String description = body.getDescription();
        String topicId = body.getTopicId();
        Flashcard flashcard = flashcardRepository.findByIdAndUserId(Integer.parseInt(flashcardId), userId);
        if (flashcard == null) {
            throw new ApiRequestException("flashcard not found", HttpStatus.BAD_REQUEST);
        }
        flashcard.setName(name);
        flashcard.setDescription(description);
        flashcard.setTopicId(topicId);
        flashcardQuestionRepository.deleteByFlashcards(flashcard);
        List<FlashcardQuestionDTO> questionList = body.getQuestions();
        for (int i = 0; i < questionList.size(); i++) {
            FlashcardsQuestion question = new FlashcardsQuestion(flashcard, questionList.get(i).getQuestion(),
                    questionList.get(i).getAnswer());
            flashcardQuestionRepository.save(question);
        }
        return flashcardRepository.save(flashcard);
    }

    public List<Flashcard> getAllFlashCard() {
        List<Flashcard> flashcards = flashcardRepository.findAll();
        Collections.sort(flashcards, new Comparator<Flashcard>() {
            @Override
            public int compare(Flashcard o1, Flashcard o2) {
                long countUnhelpful1 = o1.getReviews().stream()
                        .filter(review -> ReviewState.helpful.equals(review.getState())).count();
                long countUnhelpful2 = o2.getReviews().stream()
                        .filter(review -> ReviewState.unhelpful.equals(review.getState())).count();
                return Long.compare(countUnhelpful2, countUnhelpful1);
            }
        });
        return flashcards;
    }



    public Flashcard getFlashcardById(int flashcardId) {
        return flashcardRepository.findById(flashcardId);
    }

    public void uploadReviewFlashcard(String flashcardId, String userId, ReviewState review) {
        Flashcard flashcard = flashcardRepository.findById(Integer.parseInt(flashcardId));
        FlashcardReview reviewExist = flashcardReviewRepository.findByFlashcardsAndUserId(flashcard, userId);

        if (reviewExist != null) {
            if (reviewExist.getState().equals(review)) {
                flashcardReviewRepository.delete(reviewExist);
                return;
            } else {
                reviewExist.setState(review);
                flashcardReviewRepository.save(reviewExist);
                return;
            }

        }
        FlashcardReview flashcardReview = new FlashcardReview(flashcard, userId, review);
        flashcardReviewRepository.save(flashcardReview);

    }

    public void updateFlashcardState(Flashcard flashcard, ResourceStatus state) {
        flashcard.setState(state);
        flashcardRepository.save(flashcard);
    }
}