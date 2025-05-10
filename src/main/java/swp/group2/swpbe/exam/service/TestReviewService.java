package swp.group2.swpbe.exam.service;

import swp.group2.swpbe.exam.dto.TestReviewDTO;
import java.util.List;

public interface TestReviewService {
    List<TestReviewDTO> getReviewsByTestId(Integer testId);

    TestReviewDTO getReviewById(Integer testId, Integer reviewId);

    TestReviewDTO createReview(Integer testId, TestReviewDTO reviewDTO);

    TestReviewDTO updateReview(Integer testId, Integer reviewId, TestReviewDTO reviewDTO);

    void deleteReview(Integer testId, Integer reviewId);
}