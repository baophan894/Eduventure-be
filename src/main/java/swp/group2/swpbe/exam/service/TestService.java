package swp.group2.swpbe.exam.service;

import swp.group2.swpbe.exam.dto.TestDTO;
import swp.group2.swpbe.exam.dto.TestResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TestService {
    TestDTO createTest(TestDTO testDTO);

    TestDTO getTest(Integer id);

    TestDTO updateTest(Integer id, TestDTO testDTO);

    void deleteTest(Integer id);

    TestResponseDTO getAllTests(int page, int size, String search, Integer testLevelId, Integer typeId,
            Integer languageId);

    Page<TestDTO> searchTests(String search, Integer testLevelId, Integer typeId, Integer languageId,
            Pageable pageable);
}