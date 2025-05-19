package swp.group2.swpbe.exam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.TestTypeDTO;
import swp.group2.swpbe.exam.entities.TestType;
import swp.group2.swpbe.exam.repository.TestTypeRepository;
import swp.group2.swpbe.exam.service.TestTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestTypeServiceImpl implements TestTypeService {

    @Autowired
    private TestTypeRepository testTypeRepository;

    @Override
    public List<TestTypeDTO> getAllTestTypes() {
        List<TestType> testTypes = testTypeRepository.findAll();
        return testTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TestTypeDTO convertToDTO(TestType testType) {
        return new TestTypeDTO(testType.getId(), testType.getName());
    }
}