package swp.group2.swpbe.exam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exam.dto.QuestionTypeDTO;
import swp.group2.swpbe.exam.entities.QuestionType;
import swp.group2.swpbe.exam.repository.QuestionTypeRepository;
import swp.group2.swpbe.exam.service.QuestionTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionTypeServiceImpl implements QuestionTypeService {

    @Autowired
    private QuestionTypeRepository questionTypeRepository;

    @Override
    public List<QuestionTypeDTO> getAllQuestionTypes() {
        List<QuestionType> questionTypes = questionTypeRepository.findAll();
        return questionTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private QuestionTypeDTO convertToDTO(QuestionType questionType) {
        return new QuestionTypeDTO(questionType.getId(), questionType.getName());
    }
}