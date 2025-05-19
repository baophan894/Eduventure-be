package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.exam.dto.TestSubmissionRequestDTO;
import swp.group2.swpbe.exam.dto.TestSubmissionResponseDTO;
import swp.group2.swpbe.exam.service.TestSubmissionService;

@RestController
@RequestMapping("/api/test-submissions")
public class TestSubmissionController {

    @Autowired
    private TestSubmissionService testSubmissionService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<TestSubmissionResponseDTO> createSubmission(
            @Valid @RequestBody TestSubmissionRequestDTO requestDTO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // If token is provided, verify and get userId
            if (token != null && !token.isEmpty()) {
                String userId = authService.loginUser(token);
                return ResponseEntity.ok(testSubmissionService.createSubmission(requestDTO, Long.parseLong(userId)));
            }
            // For unauthenticated users, just evaluate without saving
            return ResponseEntity.ok(testSubmissionService.evaluateSubmission(requestDTO));
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            // Log the actual error for debugging
            e.printStackTrace();
            throw new ApiRequestException("Failed to process submission: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestSubmissionResponseDTO> getSubmissionById(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new ApiRequestException("Authentication required", HttpStatus.UNAUTHORIZED);
            }
            String userId = authService.loginUser(token);
            TestSubmissionResponseDTO submission = testSubmissionService.getSubmissionById(id, Long.parseLong(userId));
            // Always include detailed answers for single submission view
            submission.setIncludeDetailedAnswers(true);
            return ResponseEntity.ok(submission);
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to retrieve submission: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<TestSubmissionResponseDTO>> getSubmissions(
            @RequestParam(required = false) Integer testId,
            @RequestParam(required = false, defaultValue = "false") boolean includeDetailedAnswers,
            @RequestHeader(value = "Authorization", required = false) String token,
            Pageable pageable) {
        try {
            if (token == null || token.isEmpty()) {
                throw new ApiRequestException("Authentication required", HttpStatus.UNAUTHORIZED);
            }
            String userId = authService.loginUser(token);
            Page<TestSubmissionResponseDTO> submissions = testSubmissionService.getSubmissions(Long.parseLong(userId),
                    testId, pageable);

            // Set includeDetailedAnswers flag for all submissions
            submissions.getContent().forEach(dto -> dto.setIncludeDetailedAnswers(includeDetailedAnswers));

            return ResponseEntity.ok(submissions);
        } catch (ApiRequestException e) {
            // Re-throw ApiRequestException as is (includes authentication errors)
            throw e;
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log the actual error for debugging
            e.printStackTrace();
            throw new ApiRequestException("Failed to retrieve submissions: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Integer id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                throw new ApiRequestException("Authentication required", HttpStatus.UNAUTHORIZED);
            }
            String userId = authService.loginUser(token);
            testSubmissionService.deleteSubmission(id, Long.parseLong(userId));
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            throw new ApiRequestException("Invalid user ID format", HttpStatus.BAD_REQUEST);
        } catch (ApiRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to delete submission: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}