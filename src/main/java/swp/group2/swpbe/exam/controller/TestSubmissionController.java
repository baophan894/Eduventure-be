package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import swp.group2.swpbe.exam.dto.TestSubmissionRequestDTO;
import swp.group2.swpbe.exam.dto.TestSubmissionResponseDTO;
import swp.group2.swpbe.exam.service.TestSubmissionService;

@RestController
@RequestMapping("/api/test-submissions")
public class TestSubmissionController {

    @Autowired
    private TestSubmissionService testSubmissionService;

    @PostMapping
    public ResponseEntity<TestSubmissionResponseDTO> createSubmission(
            @Valid @RequestBody TestSubmissionRequestDTO requestDTO) {
        return ResponseEntity.ok(testSubmissionService.createSubmission(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestSubmissionResponseDTO> getSubmissionById(@PathVariable Integer id) {
        return ResponseEntity.ok(testSubmissionService.getSubmissionById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TestSubmissionResponseDTO>> getSubmissions(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer testId,
            Pageable pageable) {
        return ResponseEntity.ok(testSubmissionService.getSubmissions(userId, testId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Integer id) {
        testSubmissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}