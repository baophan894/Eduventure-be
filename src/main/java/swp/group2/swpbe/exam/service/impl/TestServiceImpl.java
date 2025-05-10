package swp.group2.swpbe.exam.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import swp.group2.swpbe.exam.dto.QuestionDTO;
import swp.group2.swpbe.exam.dto.QuestionOptionDTO;
import swp.group2.swpbe.exam.dto.TestDTO;
import swp.group2.swpbe.exam.dto.TestListDTO;
import swp.group2.swpbe.exam.dto.TestPartDTO;
import swp.group2.swpbe.exam.dto.TestResponseDTO;
import swp.group2.swpbe.exam.entities.Question;
import swp.group2.swpbe.exam.entities.QuestionOption;
import swp.group2.swpbe.exam.entities.QuestionType;
import swp.group2.swpbe.exam.entities.Test;
import swp.group2.swpbe.exam.entities.TestPart;
import swp.group2.swpbe.exam.entities.TestType;
import swp.group2.swpbe.exam.repository.TestRepository;
import swp.group2.swpbe.exam.service.TestService;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Override
    @Transactional
    public TestDTO createTest(TestDTO testDTO) {
        Test test = new Test();
        updateTestFromDTO(test, testDTO);
        test = testRepository.save(test);
        return convertToDTO(test);
    }

    @Override
    @Transactional(readOnly = true)
    public TestDTO getTest(Integer id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + id));
        return convertToDTO(test);
    }

    @Override
    @Transactional
    public TestDTO updateTest(Integer id, TestDTO testDTO) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + id));

        // Update basic properties
        test.setTitle(testDTO.getTitle());
        test.setDescription(testDTO.getDescription());
        test.setCoverImg(testDTO.getCoverImg());
        test.setViews(testDTO.getViews() != null ? testDTO.getViews() : 0);
        test.setRatings(testDTO.getRatings() != null ? testDTO.getRatings() : 0.0f);
        test.setReviewCount(testDTO.getReviewCount() != null ? testDTO.getReviewCount() : 0);
        test.setDuration(testDTO.getDuration());
        test.setDifficulty(testDTO.getDifficulty());
        test.setLastUpdated(LocalDateTime.now());
        test.setInstructorName(testDTO.getInstructorName());
        test.setInstructorTitle(testDTO.getInstructorTitle());
        test.setInstructorExperience(testDTO.getInstructorExperience());
        test.setInstructorDescription(testDTO.getInstructorDescription());

        // Set test type
        TestType testType = new TestType();
        testType.setId(testDTO.getTypeId());
        test.setType(testType);

        // Update test parts
        if (testDTO.getTestParts() != null) {
            // Create a map of existing test parts by name for quick lookup
            Map<String, TestPart> existingParts = test.getTestParts().stream()
                    .collect(Collectors.toMap(TestPart::getName, part -> part));

            // Process each part from the DTO
            for (TestPartDTO partDTO : testDTO.getTestParts()) {
                TestPart part;
                if (existingParts.containsKey(partDTO.getName())) {
                    // Update existing part
                    part = existingParts.get(partDTO.getName());
                    existingParts.remove(partDTO.getName());
                } else {
                    // Create new part
                    part = new TestPart();
                    test.addTestPart(part);
                }

                part.setName(partDTO.getName());
                part.setIcon(partDTO.getIcon());
                part.setDuration(partDTO.getDuration());
                part.setDescription(partDTO.getDescription());

                // Update questions
                if (partDTO.getQuestions() != null) {
                    // Create a map of existing questions by title for quick lookup
                    Map<String, Question> existingQuestions = part.getQuestions().stream()
                            .collect(Collectors.toMap(Question::getTitle, question -> question));

                    // Process each question from the DTO
                    for (QuestionDTO questionDTO : partDTO.getQuestions()) {
                        Question question;
                        if (existingQuestions.containsKey(questionDTO.getTitle())) {
                            // Update existing question
                            question = existingQuestions.get(questionDTO.getTitle());
                            existingQuestions.remove(questionDTO.getTitle());
                        } else {
                            // Create new question
                            question = new Question();
                            question.setPart(part);
                            part.getQuestions().add(question);
                        }

                        question.setTitle(questionDTO.getTitle());
                        question.setQuestionInstruction(questionDTO.getQuestionInstruction());
                        question.setAnswerInstruction(questionDTO.getAnswerInstruction());
                        question.setAudioUrl(questionDTO.getAudioUrl());
                        question.setImageUrl(questionDTO.getImageUrl());
                        question.setReadingPassage(questionDTO.getReadingPassage());
                        question.setCorrectAnswer(questionDTO.getCorrectAnswer());

                        // Set question type
                        QuestionType questionType = new QuestionType();
                        questionType.setId(questionDTO.getTypeId());
                        question.setType(questionType);

                        // Update question options
                        if (questionDTO.getQuestionOptions() != null) {
                            // Create a map of existing options by optionId for quick lookup
                            Map<String, QuestionOption> existingOptions = question.getQuestionOptions().stream()
                                    .collect(Collectors.toMap(QuestionOption::getOptionId, option -> option));

                            // Process each option from the DTO
                            for (QuestionOptionDTO optionDTO : questionDTO.getQuestionOptions()) {
                                QuestionOption option;
                                if (existingOptions.containsKey(optionDTO.getOptionId())) {
                                    // Update existing option
                                    option = existingOptions.get(optionDTO.getOptionId());
                                    existingOptions.remove(optionDTO.getOptionId());
                                } else {
                                    // Create new option
                                    option = new QuestionOption();
                                    option.setQuestion(question);
                                    question.getQuestionOptions().add(option);
                                }

                                option.setOptionId(optionDTO.getOptionId());
                                option.setText(optionDTO.getText());
                            }

                            // Remove any remaining options that weren't updated
                            for (QuestionOption option : existingOptions.values()) {
                                question.getQuestionOptions().remove(option);
                            }
                        }
                    }

                    // Remove any remaining questions that weren't updated
                    for (Question question : existingQuestions.values()) {
                        part.getQuestions().remove(question);
                    }
                }
            }

            // Remove any remaining parts that weren't updated
            for (TestPart part : existingParts.values()) {
                test.removeTestPart(part);
            }
        }

        test = testRepository.save(test);
        return convertToDTO(test);
    }

    @Override
    @Transactional
    public void deleteTest(Integer id) {
        if (!testRepository.existsById(id)) {
            throw new EntityNotFoundException("Test not found with id: " + id);
        }
        testRepository.deleteById(id);
    }

    @Override
    public TestResponseDTO getAllTests(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Test> testPage;

        if (search != null && !search.trim().isEmpty()) {
            testPage = testRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            testPage = testRepository.findAll(pageable);
        }

        List<TestListDTO> testDTOs = testPage.getContent().stream()
                .map(test -> new TestListDTO(
                        test.getId(),
                        test.getType().getId(),
                        test.getTitle(),
                        test.getDescription(),
                        test.getCoverImg(),
                        test.getViews(),
                        test.getRatings(),
                        test.getReviewCount(),
                        test.getDuration(),
                        test.getDifficulty(),
                        test.getLastUpdated(),
                        test.getInstructorName(),
                        test.getInstructorTitle()))
                .collect(Collectors.toList());

        return new TestResponseDTO(
                testDTOs,
                testPage.getNumber(),
                testPage.getTotalPages(),
                testPage.getTotalElements(),
                testPage.getSize());
    }

    private void updateTestFromDTO(Test test, TestDTO dto) {
        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setCoverImg(dto.getCoverImg());
        test.setViews(dto.getViews() != null ? dto.getViews() : 0);
        test.setRatings(dto.getRatings() != null ? dto.getRatings() : 0.0f);
        test.setReviewCount(dto.getReviewCount() != null ? dto.getReviewCount() : 0);
        test.setDuration(dto.getDuration());
        test.setDifficulty(dto.getDifficulty());
        test.setLastUpdated(LocalDateTime.now());
        test.setInstructorName(dto.getInstructorName());
        test.setInstructorTitle(dto.getInstructorTitle());
        test.setInstructorExperience(dto.getInstructorExperience());
        test.setInstructorDescription(dto.getInstructorDescription());

        // Set test type
        TestType testType = new TestType();
        testType.setId(dto.getTypeId());
        test.setType(testType);

        // Update test parts
        if (dto.getTestParts() != null) {
            List<TestPart> testParts = dto.getTestParts().stream()
                    .map(partDTO -> {
                        TestPart part = new TestPart();
                        part.setTest(test);
                        part.setName(partDTO.getName());
                        part.setIcon(partDTO.getIcon());
                        part.setDuration(partDTO.getDuration());
                        part.setDescription(partDTO.getDescription());

                        // Update questions
                        if (partDTO.getQuestions() != null) {
                            List<Question> questions = partDTO.getQuestions().stream()
                                    .map(questionDTO -> {
                                        Question question = new Question();
                                        question.setPart(part);
                                        question.setTitle(questionDTO.getTitle());
                                        question.setQuestionInstruction(questionDTO.getQuestionInstruction());
                                        question.setAnswerInstruction(questionDTO.getAnswerInstruction());
                                        question.setAudioUrl(questionDTO.getAudioUrl());
                                        question.setImageUrl(questionDTO.getImageUrl());
                                        question.setReadingPassage(questionDTO.getReadingPassage());
                                        question.setCorrectAnswer(questionDTO.getCorrectAnswer());

                                        // Set question type
                                        QuestionType questionType = new QuestionType();
                                        questionType.setId(questionDTO.getTypeId());
                                        question.setType(questionType);

                                        // Update question options
                                        if (questionDTO.getQuestionOptions() != null) {
                                            List<QuestionOption> options = questionDTO.getQuestionOptions().stream()
                                                    .map(optionDTO -> {
                                                        QuestionOption option = new QuestionOption();
                                                        option.setQuestion(question);
                                                        option.setOptionId(optionDTO.getOptionId());
                                                        option.setText(optionDTO.getText());
                                                        return option;
                                                    })
                                                    .collect(Collectors.toList());
                                            question.setQuestionOptions(options);
                                        }

                                        return question;
                                    })
                                    .collect(Collectors.toList());
                            part.setQuestions(questions);
                        }

                        return part;
                    })
                    .collect(Collectors.toList());
            test.setTestParts(testParts);
        }
    }

    private TestDTO convertToDTO(Test test) {
        TestDTO dto = new TestDTO();
        dto.setId(test.getId());
        dto.setTypeId(test.getType().getId());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setCoverImg(test.getCoverImg());
        dto.setViews(test.getViews());
        dto.setRatings(test.getRatings());
        dto.setReviewCount(test.getReviewCount());
        dto.setDuration(test.getDuration());
        dto.setDifficulty(test.getDifficulty());
        dto.setLastUpdated(test.getLastUpdated());
        dto.setInstructorName(test.getInstructorName());
        dto.setInstructorTitle(test.getInstructorTitle());
        dto.setInstructorExperience(test.getInstructorExperience());
        dto.setInstructorDescription(test.getInstructorDescription());

        // Convert test parts
        if (test.getTestParts() != null) {
            List<TestPartDTO> partDTOs = test.getTestParts().stream()
                    .map(part -> {
                        TestPartDTO partDTO = new TestPartDTO();
                        partDTO.setId(part.getId());
                        partDTO.setName(part.getName());
                        partDTO.setIcon(part.getIcon());
                        partDTO.setDuration(part.getDuration());
                        partDTO.setDescription(part.getDescription());

                        // Convert questions
                        if (part.getQuestions() != null) {
                            List<QuestionDTO> questionDTOs = part.getQuestions().stream()
                                    .map(question -> {
                                        QuestionDTO questionDTO = new QuestionDTO();
                                        questionDTO.setId(question.getId());
                                        questionDTO.setTypeId(question.getType().getId());
                                        questionDTO.setTitle(question.getTitle());
                                        questionDTO.setQuestionInstruction(question.getQuestionInstruction());
                                        questionDTO.setAnswerInstruction(question.getAnswerInstruction());
                                        questionDTO.setAudioUrl(question.getAudioUrl());
                                        questionDTO.setImageUrl(question.getImageUrl());
                                        questionDTO.setReadingPassage(question.getReadingPassage());
                                        questionDTO.setCorrectAnswer(question.getCorrectAnswer());

                                        // Convert question options
                                        if (question.getQuestionOptions() != null) {
                                            List<QuestionOptionDTO> optionDTOs = question.getQuestionOptions().stream()
                                                    .map(option -> {
                                                        QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                                                        optionDTO.setId(option.getId());
                                                        optionDTO.setOptionId(option.getOptionId());
                                                        optionDTO.setText(option.getText());
                                                        return optionDTO;
                                                    })
                                                    .collect(Collectors.toList());
                                            questionDTO.setQuestionOptions(optionDTOs);
                                        }

                                        return questionDTO;
                                    })
                                    .collect(Collectors.toList());
                            partDTO.setQuestions(questionDTOs);
                        }

                        return partDTO;
                    })
                    .collect(Collectors.toList());
            dto.setTestParts(partDTOs);
        }

        return dto;
    }
}