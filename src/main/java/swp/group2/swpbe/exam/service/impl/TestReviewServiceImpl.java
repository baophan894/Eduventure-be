package swp.group2.swpbe.exam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.entities.Test;
import swp.group2.swpbe.exam.entities.TestReview;
import swp.group2.swpbe.exam.repository.TestRepository;
import swp.group2.swpbe.exam.repository.TestReviewRepository;
import swp.group2.swpbe.exam.service.TestReviewService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestReviewServiceImpl implements TestReviewService {

    @Autowired
    private TestReviewRepository testReviewRepository;

    @Autowired
    private TestRepository testRepository;

    @Override
    public List<TestReviewDTO> getReviewsByTestId(Integer testId) {
        return testReviewRepository.findByTestId(testId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TestReviewDTO getReviewById(Integer testId, Integer reviewId) {
        TestReview review = testReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getTest().getId().equals(testId)) {
            throw new EntityNotFoundException("Review not found for the specified test");
        }

        return convertToDTO(review);
    }

    @Override
    public TestReviewDTO createReview(Integer testId, TestReviewDTO reviewDTO) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("Test not found"));

        // Check if user already reviewed this test
        if (testReviewRepository.findByTestId(testId).stream()
                .anyMatch(review -> review.getUserId().equals(reviewDTO.getUserId()))) {
            throw new DataIntegrityViolationException("User has already reviewed this test");
        }

        TestReview review = new TestReview();
        review.setTest(test);
        review.setUserId(reviewDTO.getUserId());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setReviewDate(LocalDateTime.now());

        try {
            TestReview savedReview = testReviewRepository.save(review);
            return convertToDTO(savedReview);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User has already reviewed this test");
        }
    }

    @Override
    public TestReviewDTO updateReview(Integer testId, Integer reviewId, TestReviewDTO reviewDTO) {
        TestReview review = testReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getTest().getId().equals(testId)) {
            throw new EntityNotFoundException("Review not found for the specified test");
        }

        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        TestReview updatedReview = testReviewRepository.save(review);
        return convertToDTO(updatedReview);
    }

    @Override
    public void deleteReview(Integer testId, Integer reviewId) {
        TestReview review = testReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        if (!review.getTest().getId().equals(testId)) {
            throw new EntityNotFoundException("Review not found for the specified test");
        }

        testReviewRepository.delete(review);
    }

    private TestReviewDTO convertToDTO(TestReview review) {
        return new TestReviewDTO(
                review.getId(),
                review.getTest().getId(),
                review.getUserId(),
                review.getRating(),
                review.getReviewDate(),
                review.getComment());
    }
}