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
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/tests")
public class TestReviewController {

    @Autowired
    private TestReviewService testReviewService;

    @GetMapping("/{testId}/reviews")
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

    @GetMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<TestReviewDTO> getReviewById(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId) {
        TestReviewDTO review = testReviewService.getReviewById(testId, reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping("/{testId}/reviews")
    public ResponseEntity<TestReviewDTO> createReview(
            @PathVariable Integer testId,
            @RequestBody TestReviewDTO reviewDTO) {
        TestReviewDTO createdReview = testReviewService.createReview(testId, reviewDTO);
        return ResponseEntity.ok(createdReview);
    }

    @PutMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<TestReviewDTO> updateReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId,
            @RequestBody TestReviewDTO reviewDTO) {
        TestReviewDTO updatedReview = testReviewService.updateReview(testId, reviewId, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId) {
        testReviewService.deleteReview(testId, reviewId);
        return ResponseEntity.noContent().build();
    }
}