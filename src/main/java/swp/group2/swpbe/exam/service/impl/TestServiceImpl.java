package swp.group2.swpbe.exam.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
import swp.group2.swpbe.exam.entities.TestFeature;
import swp.group2.swpbe.exam.entities.TestPart;
import swp.group2.swpbe.exam.entities.TestRequirement;
import swp.group2.swpbe.exam.entities.TestSubmission;
import swp.group2.swpbe.exam.entities.TestTargetScore;
import swp.group2.swpbe.exam.entities.TestType;
import swp.group2.swpbe.exam.entities.TestLevel;
import swp.group2.swpbe.exam.repository.TestRepository;
import swp.group2.swpbe.exam.repository.TestReviewRepository;
import swp.group2.swpbe.exam.repository.TestSubmissionRepository;
import swp.group2.swpbe.exam.repository.TestLevelRepository;
import swp.group2.swpbe.exam.service.TestService;

@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestReviewRepository testReviewRepository;

    @Autowired
    private TestSubmissionRepository testSubmissionRepository;

    @Autowired
    private TestLevelRepository testLevelRepository;

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
        // Increment views
        test.setViews(test.getViews() != null ? test.getViews() + 1 : 1);
        testRepository.save(test);
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

        // Handle test level
        if (testDTO.getTestLevel() != null) {
            TestLevel testLevel = testLevelRepository.findByName(testDTO.getTestLevel())
                    .orElseGet(() -> {
                        TestLevel newLevel = new TestLevel();
                        newLevel.setName(testDTO.getTestLevel());
                        return testLevelRepository.save(newLevel);
                    });
            test.setTestLevel(testLevel);
        }

        test.setInstructorName(testDTO.getInstructorName());
        test.setInstructorTitle(testDTO.getInstructorTitle());
        test.setInstructorExperience(testDTO.getInstructorExperience());
        test.setInstructorDescription(testDTO.getInstructorDescription());
        test.setInstructorAvatar(testDTO.getInstructorAvatar());

        // Set test type
        TestType testType = new TestType();
        testType.setId(testDTO.getTypeId());
        test.setType(testType);

        // Update test features
        if (testDTO.getTestFeatures() != null) {
            // Create a map of existing features by feature text for quick lookup
            Map<String, TestFeature> existingFeatures = test.getTestFeatures().stream()
                    .collect(Collectors.toMap(TestFeature::getFeature, feature -> feature));

            // Process each feature from the DTO
            for (String featureText : testDTO.getTestFeatures()) {
                TestFeature feature;
                if (existingFeatures.containsKey(featureText)) {
                    // Update existing feature
                    feature = existingFeatures.get(featureText);
                    existingFeatures.remove(featureText);
                } else {
                    // Create new feature
                    feature = new TestFeature();
                    test.addTestFeature(feature);
                }
                feature.setFeature(featureText);
            }

            // Remove any remaining features that weren't updated
            for (TestFeature feature : existingFeatures.values()) {
                test.removeTestFeature(feature);
            }
        }

        // Update test requirements
        if (testDTO.getTestRequirements() != null) {
            // Create a map of existing requirements by requirement text for quick lookup
            Map<String, TestRequirement> existingRequirements = test.getTestRequirements().stream()
                    .collect(Collectors.toMap(TestRequirement::getRequirement, requirement -> requirement));

            // Process each requirement from the DTO
            for (String requirementText : testDTO.getTestRequirements()) {
                TestRequirement requirement;
                if (existingRequirements.containsKey(requirementText)) {
                    // Update existing requirement
                    requirement = existingRequirements.get(requirementText);
                    existingRequirements.remove(requirementText);
                } else {
                    // Create new requirement
                    requirement = new TestRequirement();
                    test.addTestRequirement(requirement);
                }
                requirement.setRequirement(requirementText);
            }

            // Remove any remaining requirements that weren't updated
            for (TestRequirement requirement : existingRequirements.values()) {
                test.removeTestRequirement(requirement);
            }
        }

        // Handle target scores
        Map<String, TestTargetScore> existingTargetScores = test.getTestTargetScores().stream()
                .collect(Collectors.toMap(TestTargetScore::getScore, Function.identity()));

        if (testDTO.getTestTargetScores() != null) {
            for (String scoreText : testDTO.getTestTargetScores()) {
                if (existingTargetScores.containsKey(scoreText)) {
                    // Update existing target score
                    TestTargetScore existingScore = existingTargetScores.get(scoreText);
                    existingScore.setScore(scoreText);
                    existingTargetScores.remove(scoreText);
                } else {
                    // Create new target score
                    TestTargetScore newScore = new TestTargetScore();
                    newScore.setTest(test);
                    newScore.setScore(scoreText);
                    test.getTestTargetScores().add(newScore);
                }
            }
        }

        // Remove any remaining target scores that weren't in the new list
        for (TestTargetScore oldScore : existingTargetScores.values()) {
            test.getTestTargetScores().remove(oldScore);
        }

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
                part.setOrder(partDTO.getOrder());
                part.setAudioUrl(partDTO.getAudioUrl());

                // Initialize questions list if null
                if (part.getQuestions() == null) {
                    part.setQuestions(new ArrayList<>());
                }

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
                        question.setImageUrl(questionDTO.getImageUrl());
                        question.setReadingPassage(questionDTO.getReadingPassage());
                        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
                        question.setOrder(questionDTO.getOrder());

                        // Set question type
                        QuestionType questionType = new QuestionType();
                        questionType.setId(questionDTO.getTypeId());
                        question.setType(questionType);

                        // Initialize question options list if null
                        if (question.getQuestionOptions() == null) {
                            question.setQuestionOptions(new ArrayList<>());
                        }

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
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Test not found with id: " + id));

        // First delete all test submissions and their related data
        List<TestSubmission> submissions = testSubmissionRepository.findByTestId(id);
        for (TestSubmission submission : submissions) {
            // Clear submitted answers first
            submission.getSubmittedAnswers().clear();
            // Clear submission parts
            submission.getSubmissionParts().clear();
            // Delete the submission
            testSubmissionRepository.delete(submission);
        }

        // Now we can safely delete the test
        testRepository.delete(test);
    }

    @Override
    public TestResponseDTO getAllTests(int page, int size, String search, Integer testLevelId, Integer typeId,
            Integer languageId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Test> testPage;

        if (search != null && !search.trim().isEmpty()) {
            if (testLevelId != null && typeId != null && languageId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageIdAndTypeId(
                        search, testLevelId, languageId, typeId, pageable);
            } else if (testLevelId != null && languageId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageId(
                        search, testLevelId, languageId, pageable);
            } else if (typeId != null && languageId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelLanguageIdAndTypeId(
                        search, languageId, typeId, pageable);
            } else if (testLevelId != null && typeId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTypeId(
                        search, testLevelId, typeId, pageable);
            } else if (languageId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelLanguageId(
                        search, languageId, pageable);
            } else if (testLevelId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTestLevelId(
                        search, testLevelId, pageable);
            } else if (typeId != null) {
                testPage = testRepository.findByTitleContainingIgnoreCaseAndTypeId(
                        search, typeId, pageable);
            } else {
                testPage = testRepository.findByTitleContainingIgnoreCase(search, pageable);
            }
        } else {
            if (testLevelId != null && typeId != null && languageId != null) {
                testPage = testRepository.findByTestLevelIdAndTestLevelLanguageIdAndTypeId(
                        testLevelId, languageId, typeId, pageable);
            } else if (testLevelId != null && languageId != null) {
                testPage = testRepository.findByTestLevelIdAndTestLevelLanguageId(
                        testLevelId, languageId, pageable);
            } else if (typeId != null && languageId != null) {
                testPage = testRepository.findByTestLevelLanguageIdAndTypeId(
                        languageId, typeId, pageable);
            } else if (testLevelId != null && typeId != null) {
                testPage = testRepository.findByTestLevelIdAndTypeId(
                        testLevelId, typeId, pageable);
            } else if (languageId != null) {
                testPage = testRepository.findByTestLevelLanguageId(languageId, pageable);
            } else if (testLevelId != null) {
                testPage = testRepository.findByTestLevelId(testLevelId, pageable);
            } else if (typeId != null) {
                testPage = testRepository.findByTypeId(typeId, pageable);
            } else {
                testPage = testRepository.findAll(pageable);
            }
        }

        List<TestListDTO> testDTOs = testPage.getContent().stream()
                .map(this::convertToTestListDTO)
                .collect(Collectors.toList());

        return new TestResponseDTO(
                testDTOs,
                testPage.getNumber(),
                testPage.getTotalPages(),
                testPage.getTotalElements(),
                testPage.getSize());
    }

    @Override
    public Page<TestDTO> searchTests(String search, Integer testLevelId, Integer typeId, Integer languageId,
            Pageable pageable) {
        Page<Test> tests;
        if (search != null && !search.trim().isEmpty()) {
            if (testLevelId != null && typeId != null && languageId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageIdAndTypeId(
                        search, testLevelId, languageId, typeId, pageable);
            } else if (testLevelId != null && languageId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageId(
                        search, testLevelId, languageId, pageable);
            } else if (typeId != null && languageId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelLanguageIdAndTypeId(
                        search, languageId, typeId, pageable);
            } else if (testLevelId != null && typeId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelIdAndTypeId(
                        search, testLevelId, typeId, pageable);
            } else if (languageId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelLanguageId(
                        search, languageId, pageable);
            } else if (testLevelId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTestLevelId(
                        search, testLevelId, pageable);
            } else if (typeId != null) {
                tests = testRepository.findByTitleContainingIgnoreCaseAndTypeId(
                        search, typeId, pageable);
            } else {
                tests = testRepository.findByTitleContainingIgnoreCase(search, pageable);
            }
        } else {
            if (testLevelId != null && typeId != null && languageId != null) {
                tests = testRepository.findByTestLevelIdAndTestLevelLanguageIdAndTypeId(
                        testLevelId, languageId, typeId, pageable);
            } else if (testLevelId != null && languageId != null) {
                tests = testRepository.findByTestLevelIdAndTestLevelLanguageId(
                        testLevelId, languageId, pageable);
            } else if (typeId != null && languageId != null) {
                tests = testRepository.findByTestLevelLanguageIdAndTypeId(
                        languageId, typeId, pageable);
            } else if (testLevelId != null && typeId != null) {
                tests = testRepository.findByTestLevelIdAndTypeId(
                        testLevelId, typeId, pageable);
            } else if (languageId != null) {
                tests = testRepository.findByTestLevelLanguageId(languageId, pageable);
            } else if (testLevelId != null) {
                tests = testRepository.findByTestLevelId(testLevelId, pageable);
            } else if (typeId != null) {
                tests = testRepository.findByTypeId(typeId, pageable);
            } else {
                tests = testRepository.findAll(pageable);
            }
        }
        return tests.map(this::convertToDTO);
    }

    private void updateTestFromDTO(Test test, TestDTO dto) {
        test.setTitle(dto.getTitle());
        test.setDescription(dto.getDescription());
        test.setCoverImg(dto.getCoverImg());
        test.setViews(dto.getViews());

        // Handle test level
        if (dto.getTestLevel() != null) {
            TestLevel testLevel = testLevelRepository.findByName(dto.getTestLevel())
                    .orElseGet(() -> {
                        TestLevel newLevel = new TestLevel();
                        newLevel.setName(dto.getTestLevel());
                        return testLevelRepository.save(newLevel);
                    });
            test.setTestLevel(testLevel);
        }

        test.setInstructorName(dto.getInstructorName());
        test.setInstructorTitle(dto.getInstructorTitle());
        test.setInstructorExperience(dto.getInstructorExperience());
        test.setInstructorDescription(dto.getInstructorDescription());
        test.setInstructorAvatar(dto.getInstructorAvatar());

        // Set test type
        TestType testType = new TestType();
        testType.setId(dto.getTypeId());
        test.setType(testType);

        // Update test features
        if (dto.getTestFeatures() != null) {
            // Create a map of existing features by feature text for quick lookup
            Map<String, TestFeature> existingFeatures = test.getTestFeatures().stream()
                    .collect(Collectors.toMap(TestFeature::getFeature, feature -> feature));

            // Process each feature from the DTO
            for (String featureText : dto.getTestFeatures()) {
                TestFeature feature;
                if (existingFeatures.containsKey(featureText)) {
                    // Update existing feature
                    feature = existingFeatures.get(featureText);
                    existingFeatures.remove(featureText);
                } else {
                    // Create new feature
                    feature = new TestFeature();
                    test.addTestFeature(feature);
                }
                feature.setFeature(featureText);
            }

            // Remove any remaining features that weren't updated
            for (TestFeature feature : existingFeatures.values()) {
                test.removeTestFeature(feature);
            }
        }

        // Update test requirements
        if (dto.getTestRequirements() != null) {
            // Create a map of existing requirements by requirement text for quick lookup
            Map<String, TestRequirement> existingRequirements = test.getTestRequirements().stream()
                    .collect(Collectors.toMap(TestRequirement::getRequirement, requirement -> requirement));

            // Process each requirement from the DTO
            for (String requirementText : dto.getTestRequirements()) {
                TestRequirement requirement;
                if (existingRequirements.containsKey(requirementText)) {
                    // Update existing requirement
                    requirement = existingRequirements.get(requirementText);
                    existingRequirements.remove(requirementText);
                } else {
                    // Create new requirement
                    requirement = new TestRequirement();
                    test.addTestRequirement(requirement);
                }
                requirement.setRequirement(requirementText);
            }

            // Remove any remaining requirements that weren't updated
            for (TestRequirement requirement : existingRequirements.values()) {
                test.removeTestRequirement(requirement);
            }
        }

        // Handle target scores
        Map<String, TestTargetScore> existingTargetScores = test.getTestTargetScores().stream()
                .collect(Collectors.toMap(TestTargetScore::getScore, Function.identity()));

        if (dto.getTestTargetScores() != null) {
            for (String scoreText : dto.getTestTargetScores()) {
                if (existingTargetScores.containsKey(scoreText)) {
                    // Update existing target score
                    TestTargetScore existingScore = existingTargetScores.get(scoreText);
                    existingScore.setScore(scoreText);
                    existingTargetScores.remove(scoreText);
                } else {
                    // Create new target score
                    TestTargetScore newScore = new TestTargetScore();
                    newScore.setTest(test);
                    newScore.setScore(scoreText);
                    test.getTestTargetScores().add(newScore);
                }
            }
        }

        // Remove any remaining target scores that weren't in the new list
        for (TestTargetScore oldScore : existingTargetScores.values()) {
            test.getTestTargetScores().remove(oldScore);
        }

        // Update test parts
        if (dto.getTestParts() != null) {
            // Create a map of existing test parts by name for quick lookup
            Map<String, TestPart> existingParts = test.getTestParts().stream()
                    .collect(Collectors.toMap(TestPart::getName, part -> part));

            // Process each part from the DTO
            for (TestPartDTO partDTO : dto.getTestParts()) {
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
                part.setOrder(partDTO.getOrder());
                part.setAudioUrl(partDTO.getAudioUrl());

                // Initialize questions list if null
                if (part.getQuestions() == null) {
                    part.setQuestions(new ArrayList<>());
                }

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
                        question.setImageUrl(questionDTO.getImageUrl());
                        question.setReadingPassage(questionDTO.getReadingPassage());
                        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
                        question.setOrder(questionDTO.getOrder());

                        // Set question type
                        QuestionType questionType = new QuestionType();
                        questionType.setId(questionDTO.getTypeId());
                        question.setType(questionType);

                        // Initialize question options list if null
                        if (question.getQuestionOptions() == null) {
                            question.setQuestionOptions(new ArrayList<>());
                        }

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
    }

    private TestDTO convertToDTO(Test test) {
        TestDTO dto = new TestDTO();
        dto.setId(test.getId());
        dto.setTypeId(test.getType().getId());
        dto.setTypeName(test.getType().getName());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setCoverImg(test.getCoverImg());
        dto.setViews(test.getViews());
        dto.setTestLevel(test.getTestLevel() != null ? test.getTestLevel().getName() : null);
        dto.setTestLevelId(test.getTestLevel() != null ? test.getTestLevel().getId() : null);
        dto.setInstructorName(test.getInstructorName());
        dto.setInstructorTitle(test.getInstructorTitle());
        dto.setInstructorExperience(test.getInstructorExperience());
        dto.setInstructorDescription(test.getInstructorDescription());
        dto.setInstructorAvatar(test.getInstructorAvatar());

        // Calculate total duration from test parts
        int totalDuration = test.getTestParts().stream()
                .mapToInt(TestPart::getDuration)
                .sum();
        dto.setDuration(totalDuration);

        // Calculate and set ratings and review count
        Double averageRating = testReviewRepository.getAverageRatingByTestId(test.getId());
        long reviewCount = testReviewRepository.countByTestId(test.getId());
        dto.setRatings(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
        dto.setReviewCount((int) reviewCount);

        // Convert test features
        dto.setTestFeatures(test.getTestFeatures().stream()
                .map(TestFeature::getFeature)
                .collect(Collectors.toList()));

        // Convert test requirements
        dto.setTestRequirements(test.getTestRequirements().stream()
                .map(TestRequirement::getRequirement)
                .collect(Collectors.toList()));

        // Convert test target scores
        dto.setTestTargetScores(test.getTestTargetScores().stream()
                .map(TestTargetScore::getScore)
                .collect(Collectors.toList()));

        // Convert test parts
        dto.setTestParts(test.getTestParts().stream()
                .map(this::convertToTestPartDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private TestListDTO convertToTestListDTO(Test test) {
        TestListDTO dto = new TestListDTO();
        dto.setId(test.getId());
        dto.setTypeId(test.getType().getId());
        dto.setTypeName(test.getType().getName());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setCoverImg(test.getCoverImg());
        dto.setViews(test.getViews());
        dto.setTestLevel(test.getTestLevel() != null ? test.getTestLevel().getName() : null);
        dto.setTestLevelId(test.getTestLevel() != null ? test.getTestLevel().getId() : null);
        dto.setInstructorName(test.getInstructorName());
        dto.setInstructorTitle(test.getInstructorTitle());
        dto.setInstructorExperience(test.getInstructorExperience());
        dto.setInstructorDescription(test.getInstructorDescription());
        dto.setInstructorAvatar(test.getInstructorAvatar());

        // Calculate total duration from test parts
        int totalDuration = test.getTestParts().stream()
                .mapToInt(TestPart::getDuration)
                .sum();
        dto.setDuration(totalDuration);

        // Calculate and set ratings and review count
        Double averageRating = testReviewRepository.getAverageRatingByTestId(test.getId());
        long reviewCount = testReviewRepository.countByTestId(test.getId());
        dto.setRatings(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0);
        dto.setReviewCount((int) reviewCount);

        // Convert test features
        dto.setTestFeatures(test.getTestFeatures().stream()
                .map(TestFeature::getFeature)
                .collect(Collectors.toList()));

        // Convert test requirements
        dto.setTestRequirements(test.getTestRequirements().stream()
                .map(TestRequirement::getRequirement)
                .collect(Collectors.toList()));

        // Convert test target scores
        dto.setTestTargetScores(test.getTestTargetScores().stream()
                .map(TestTargetScore::getScore)
                .collect(Collectors.toList()));

        return dto;
    }

    private TestPartDTO convertToTestPartDTO(TestPart part) {
        TestPartDTO partDTO = new TestPartDTO();
        partDTO.setId(part.getId());
        partDTO.setName(part.getName());
        partDTO.setIcon(part.getIcon());
        partDTO.setDuration(part.getDuration());
        partDTO.setDescription(part.getDescription());
        partDTO.setOrder(part.getOrder());
        partDTO.setAudioUrl(part.getAudioUrl());

        // Convert questions
        partDTO.setQuestions(part.getQuestions().stream()
                .map(question -> {
                    QuestionDTO questionDTO = new QuestionDTO();
                    questionDTO.setId(question.getId());
                    questionDTO.setTitle(question.getTitle());
                    questionDTO.setQuestionInstruction(question.getQuestionInstruction());
                    questionDTO.setAnswerInstruction(question.getAnswerInstruction());
                    questionDTO.setImageUrl(question.getImageUrl());
                    questionDTO.setReadingPassage(question.getReadingPassage());
                    questionDTO.setCorrectAnswer(question.getCorrectAnswer());
                    questionDTO.setOrder(question.getOrder());
                    questionDTO.setTypeId(question.getType().getId());
                    questionDTO.setTypeName(question.getType().getName());

                    // Convert question options
                    questionDTO.setQuestionOptions(question.getQuestionOptions().stream()
                            .map(option -> {
                                QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                                optionDTO.setId(option.getId());
                                optionDTO.setOptionId(option.getOptionId());
                                optionDTO.setText(option.getText());
                                return optionDTO;
                            })
                            .collect(Collectors.toList()));

                    return questionDTO;
                })
                .collect(Collectors.toList()));

        return partDTO;
    }
}