package swp.group2.swpbe.exam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.dto.TestReviewFilterDTO;
import swp.group2.swpbe.exam.dto.TestReviewResponseDTO;
import swp.group2.swpbe.exam.entities.Test;
import swp.group2.swpbe.exam.entities.TestReview;
import swp.group2.swpbe.exam.repository.TestRepository;
import swp.group2.swpbe.exam.repository.TestReviewRepository;
import swp.group2.swpbe.exam.service.TestReviewService;
import swp.group2.swpbe.user.UserRepository;
import swp.group2.swpbe.user.entities.User;
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

        @Autowired
        private UserRepository userRepository;

        @Override
        public TestReviewResponseDTO getReviewsByTestId(Integer testId, Pageable pageable, TestReviewFilterDTO filter) {
                Test test = testRepository.findById(testId)
                                .orElseThrow(() -> new RuntimeException("Test not found"));

                Page<TestReview> reviewPage = testReviewRepository.findByTestIdAndFilters(
                                testId,
                                filter.getRating(),
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate(),
                                pageable);

                List<TestReviewDTO> reviewDTOs = reviewPage.getContent().stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());

                // Get rating statistics with filters
                long totalReviews = testReviewRepository.countByTestIdAndFilters(
                                testId,
                                filter.getRating(),
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                Double averageRating = testReviewRepository.getAverageRatingByTestIdAndFilters(
                                testId,
                                filter.getRating(),
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                long fiveStarCount = testReviewRepository.countByTestIdAndRatingAndFilters(
                                testId, 5,
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                long fourStarCount = testReviewRepository.countByTestIdAndRatingAndFilters(
                                testId, 4,
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                long threeStarCount = testReviewRepository.countByTestIdAndRatingAndFilters(
                                testId, 3,
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                long twoStarCount = testReviewRepository.countByTestIdAndRatingAndFilters(
                                testId, 2,
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                long oneStarCount = testReviewRepository.countByTestIdAndRatingAndFilters(
                                testId, 1,
                                filter.getUserId(),
                                filter.getComment(),
                                filter.getFromDate(),
                                filter.getToDate());

                return TestReviewResponseDTO.builder()
                                .reviews(reviewDTOs)
                                .currentPage(reviewPage.getNumber())
                                .totalPages(reviewPage.getTotalPages())
                                .totalItems(reviewPage.getTotalElements())
                                .pageSize(reviewPage.getSize())
                                .averageRating(averageRating != null ? averageRating : 0.0)
                                .totalReviews(totalReviews)
                                .fiveStarCount(fiveStarCount)
                                .fourStarCount(fourStarCount)
                                .threeStarCount(threeStarCount)
                                .twoStarCount(twoStarCount)
                                .oneStarCount(oneStarCount)
                                .build();
        }

        @Override
        public TestReviewDTO getReviewById(Integer testId, Integer reviewId) {
                TestReview review = testReviewRepository.findById(reviewId)
                                .orElseThrow(() -> new RuntimeException("Review not found"));

                if (!review.getTest().getId().equals(testId)) {
                        throw new RuntimeException("Review does not belong to the specified test");
                }

                return convertToDTO(review);
        }

        @Override
        public TestReviewDTO createReview(Integer testId, TestReviewDTO reviewDTO) {
                Test test = testRepository.findById(testId)
                                .orElseThrow(() -> new RuntimeException("Test not found"));

                TestReview review = new TestReview();
                review.setTest(test);
                review.setUserId(reviewDTO.getUserId());
                review.setRating(reviewDTO.getRating());
                review.setComment(reviewDTO.getComment());
                review.setReviewDate(LocalDateTime.now());

                TestReview savedReview = testReviewRepository.save(review);
                return convertToDTO(savedReview);
        }

        @Override
        public TestReviewDTO updateReview(Integer testId, Integer reviewId, TestReviewDTO reviewDTO) {
                TestReview review = testReviewRepository.findById(reviewId)
                                .orElseThrow(() -> new RuntimeException("Review not found"));

                if (!review.getTest().getId().equals(testId)) {
                        throw new RuntimeException("Review does not belong to the specified test");
                }

                review.setRating(reviewDTO.getRating());
                review.setComment(reviewDTO.getComment());

                TestReview updatedReview = testReviewRepository.save(review);
                return convertToDTO(updatedReview);
        }

        @Override
        public void deleteReview(Integer testId, Integer reviewId) {
                TestReview review = testReviewRepository.findById(reviewId)
                                .orElseThrow(() -> new RuntimeException("Review not found"));

                if (!review.getTest().getId().equals(testId)) {
                        throw new RuntimeException("Review does not belong to the specified test");
                }

                testReviewRepository.delete(review);
        }

        private TestReviewDTO convertToDTO(TestReview review) {
                User user = userRepository.findById(review.getUserId().intValue());
                String userFullName = user != null ? user.getFullName() : null;
                String userAvatarUrl = user != null ? user.getAvatarUrl() : null;

                TestReviewDTO dto = new TestReviewDTO();
                dto.setId(review.getId());
                dto.setTestId(review.getTest().getId());
                dto.setUserId(review.getUserId());
                dto.setRating(review.getRating());
                dto.setComment(review.getComment());
                dto.setReviewDate(review.getReviewDate());
                dto.setUserFullName(userFullName);
                dto.setUserAvatarUrl(userAvatarUrl);

                return dto;
        }
}