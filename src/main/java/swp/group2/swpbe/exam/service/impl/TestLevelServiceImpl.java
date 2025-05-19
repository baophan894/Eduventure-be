package swp.group2.swpbe.exam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.LanguageDTO;
import swp.group2.swpbe.exam.dto.TestLevelDTO;
import swp.group2.swpbe.exam.entities.TestLevel;
import swp.group2.swpbe.exam.repository.TestLevelRepository;
import swp.group2.swpbe.exam.service.TestLevelService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestLevelServiceImpl implements TestLevelService {

    @Autowired
    private TestLevelRepository testLevelRepository;

    @Override
    public List<TestLevelDTO> getAllTestLevels() {
        List<TestLevel> testLevels = testLevelRepository.findAll();
        return testLevels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TestLevelDTO convertToDTO(TestLevel testLevel) {
        LanguageDTO languageDTO = new LanguageDTO(
                testLevel.getLanguage().getId(),
                testLevel.getLanguage().getCode(),
                testLevel.getLanguage().getName());
        return new TestLevelDTO(testLevel.getId(), testLevel.getName(), languageDTO);
    }
}