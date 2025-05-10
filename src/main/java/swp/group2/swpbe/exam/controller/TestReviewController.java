package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.service.TestReviewService;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestReviewController {

    @Autowired
    private TestReviewService testReviewService;

    @GetMapping("/{testId}/reviews")
    public ResponseEntity<List<TestReviewDTO>> getReviewsByTestId(@PathVariable Integer testId) {
        return ResponseEntity.ok(testReviewService.getReviewsByTestId(testId));
    }

    @GetMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<TestReviewDTO> getReviewById(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId) {
        return ResponseEntity.ok(testReviewService.getReviewById(testId, reviewId));
    }

    @PostMapping("/{testId}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable Integer testId,
            @RequestBody TestReviewDTO reviewDTO) {
        try {
            return ResponseEntity.ok(testReviewService.createReview(testId, reviewDTO));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User has already reviewed this test");
        }
    }

    @PutMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<TestReviewDTO> updateReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId,
            @RequestBody TestReviewDTO reviewDTO) {
        return ResponseEntity.ok(testReviewService.updateReview(testId, reviewId, reviewDTO));
    }

    @DeleteMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId) {
        testReviewService.deleteReview(testId, reviewId);
        return ResponseEntity.noContent().build();
    }
}