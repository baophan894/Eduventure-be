package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.dto.TestReviewFilterDTO;
import swp.group2.swpbe.exam.dto.TestReviewResponseDTO;
import swp.group2.swpbe.exam.service.TestReviewService;
import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.exception.ApiRequestException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/tests/{testId}/reviews")
public class TestReviewController {

    @Autowired
    private TestReviewService testReviewService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<TestReviewResponseDTO> getReviewsByTestId(
            @PathVariable Integer testId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {

        // Ensure size is between 1 and 50
        size = Math.min(50, Math.max(1, size));

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        TestReviewFilterDTO filter = new TestReviewFilterDTO();
        filter.setRating(rating);
        filter.setUserId(userId);
        filter.setComment(comment);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);

        TestReviewResponseDTO response = testReviewService.getReviewsByTestId(testId, pageable, filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<TestReviewDTO> getReviewById(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId) {
        TestReviewDTO review = testReviewService.getReviewById(testId, reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping
    public ResponseEntity<TestReviewDTO> createReview(
            @PathVariable Integer testId,
            @RequestBody TestReviewDTO reviewDTO,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = authService.loginUser(token);
            reviewDTO.setUserId(Long.parseLong(userId));
            TestReviewDTO createdReview = testReviewService.createReview(testId, reviewDTO);
            return ResponseEntity.ok(createdReview);
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create review: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<TestReviewDTO> updateReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId,
            @RequestBody TestReviewDTO reviewDTO,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = authService.loginUser(token);
            reviewDTO.setUserId(Long.parseLong(userId));
            TestReviewDTO updatedReview = testReviewService.updateReview(testId, reviewId, reviewDTO);
            return ResponseEntity.ok(updatedReview);
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update review: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = authService.loginUser(token);
            testReviewService.deleteReview(testId, reviewId);
            return ResponseEntity.noContent().build();
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to delete review: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkUserReview(
            @PathVariable Integer testId,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = authService.loginUser(token);
            boolean hasReviewed = testReviewService.hasUserReviewed(testId, Long.parseLong(userId));
            return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to check review status: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}