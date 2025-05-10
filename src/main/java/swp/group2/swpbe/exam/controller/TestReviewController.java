package swp.group2.swpbe.exam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import swp.group2.swpbe.exam.dto.TestReviewDTO;
import swp.group2.swpbe.exam.service.TestReviewService;

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
    public ResponseEntity<TestReviewDTO> createReview(
            @PathVariable Integer testId,
            @Valid @RequestBody TestReviewDTO reviewDTO) {
        return ResponseEntity.ok(testReviewService.createReview(testId, reviewDTO));
    }

    @PutMapping("/{testId}/reviews/{reviewId}")
    public ResponseEntity<TestReviewDTO> updateReview(
            @PathVariable Integer testId,
            @PathVariable Integer reviewId,
            @Valid @RequestBody TestReviewDTO reviewDTO) {
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