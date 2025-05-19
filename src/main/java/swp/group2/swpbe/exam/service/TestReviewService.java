package swp.group2.swpbe.exam.service;

import org.springframework.data.domain.Pageable;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.dto.TestReviewFilterDTO;
import swp.group2.swpbe.exam.dto.TestReviewResponseDTO;
import java.util.List;

public interface TestReviewService {
    TestReviewResponseDTO getReviewsByTestId(Integer testId, Pageable pageable, TestReviewFilterDTO filter);

    TestReviewDTO getReviewById(Integer testId, Integer reviewId);

    TestReviewDTO createReview(Integer testId, TestReviewDTO reviewDTO);

    TestReviewDTO updateReview(Integer testId, Integer reviewId, TestReviewDTO reviewDTO);

    void deleteReview(Integer testId, Integer reviewId);
}