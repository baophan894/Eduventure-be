package swp.group2.swpbe.exam.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import swp.group2.swpbe.exam.dto.TestSubmissionRequestDTO;
import swp.group2.swpbe.exam.dto.TestSubmissionResponseDTO;

public interface TestSubmissionService {
    TestSubmissionResponseDTO createSubmission(TestSubmissionRequestDTO requestDTO);

    TestSubmissionResponseDTO getSubmissionById(Integer id);

    Page<TestSubmissionResponseDTO> getSubmissions(Long userId, Integer testId, Pageable pageable);

    void deleteSubmission(Integer id);
}