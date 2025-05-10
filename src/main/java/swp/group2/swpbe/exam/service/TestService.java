package swp.group2.swpbe.exam.service;

import swp.group2.swpbe.exam.dto.TestDTO;
import swp.group2.swpbe.exam.dto.TestResponseDTO;

public interface TestService {
    TestDTO createTest(TestDTO testDTO);

    TestDTO getTest(Integer id);

    TestDTO updateTest(Integer id, TestDTO testDTO);

    void deleteTest(Integer id);

    TestResponseDTO getAllTests(int page, int size, String search);
}