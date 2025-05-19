package swp.group2.swpbe.exam.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import swp.group2.swpbe.exam.dto.QuestionOptionDTO;
import swp.group2.swpbe.exam.dto.SelectedOptionDTO;
import swp.group2.swpbe.exam.dto.SubmittedAnswerRequestDTO;
import swp.group2.swpbe.exam.dto.SubmittedAnswerResponseDTO;
import swp.group2.swpbe.exam.dto.TestPartSubmissionDTO;
import swp.group2.swpbe.exam.dto.TestSubmissionRequestDTO;
import swp.group2.swpbe.exam.dto.TestSubmissionResponseDTO;
import swp.group2.swpbe.exam.entities.Question;
import swp.group2.swpbe.exam.entities.QuestionType;
import swp.group2.swpbe.exam.entities.SubmittedAnswer;
import swp.group2.swpbe.exam.entities.Test;
import swp.group2.swpbe.exam.entities.TestPart;
import swp.group2.swpbe.exam.entities.TestSubmission;
import swp.group2.swpbe.exam.entities.TestSubmissionPart;
import swp.group2.swpbe.exam.exception.TestSubmissionException;
import swp.group2.swpbe.exam.repository.QuestionRepository;
import swp.group2.swpbe.exam.repository.TestRepository;
import swp.group2.swpbe.exam.repository.TestSubmissionRepository;
import swp.group2.swpbe.exam.service.TestSubmissionService;

@Service
public class TestSubmissionServiceImpl implements TestSubmissionService {

    @Autowired
    private TestSubmissionRepository testSubmissionRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    @Transactional
    public TestSubmissionResponseDTO createSubmission(TestSubmissionRequestDTO requestDTO, Long userId) {
        // Validate test exists
        Test test = testRepository.findById(requestDTO.getTestId())
                .orElseThrow(
                        () -> new TestSubmissionException("Test with ID " + requestDTO.getTestId() + " not found"));

        // Validate submission time
        if (requestDTO.getSubmittedAt() == null) {
            requestDTO.setSubmittedAt(LocalDateTime.now());
        }

        // Validate time spent
        if (requestDTO.getTimeSpent() == null || requestDTO.getTimeSpent() < 0) {
            throw new TestSubmissionException("Time spent must be a positive number");
        }

        // Get questions from selected test parts
        Set<Question> testQuestions;
        Set<TestPart> selectedParts;
        if (requestDTO.getPartIds() != null && !requestDTO.getPartIds().isEmpty()) {
            // Get questions only from selected parts
            selectedParts = test.getTestParts().stream()
                    .filter(part -> requestDTO.getPartIds().contains(part.getId()))
                    .collect(Collectors.toSet());

            if (selectedParts.isEmpty()) {
                throw new TestSubmissionException("No valid test parts selected");
            }

            testQuestions = selectedParts.stream()
                    .flatMap(part -> part.getQuestions().stream())
                    .collect(Collectors.toSet());

            if (testQuestions.isEmpty()) {
                throw new TestSubmissionException("No questions found in the selected test parts");
            }
        } else {
            // Get all questions if no parts specified
            selectedParts = new HashSet<>(test.getTestParts());
            testQuestions = selectedParts.stream()
                    .flatMap(part -> part.getQuestions().stream())
                    .collect(Collectors.toSet());

            if (testQuestions.isEmpty()) {
                throw new TestSubmissionException("Test has no questions");
            }
        }

        // Create a temporary submission object for conversion
        TestSubmission tempSubmission = new TestSubmission();
        tempSubmission.setTest(test);
        tempSubmission.setUserId(userId);
        tempSubmission.setSubmittedAt(requestDTO.getSubmittedAt());
        tempSubmission.setStatus("EVALUATED");
        tempSubmission.setTimeSpent(requestDTO.getTimeSpent());

        // Add selected parts to submission
        for (TestPart part : selectedParts) {
            TestSubmissionPart submissionPart = new TestSubmissionPart();
            submissionPart.setPart(part);
            tempSubmission.addSubmissionPart(submissionPart);
        }

        // Process submitted answers if any
        if (requestDTO.getSubmittedAnswers() != null && !requestDTO.getSubmittedAnswers().isEmpty()) {
            for (SubmittedAnswerRequestDTO answerDTO : requestDTO.getSubmittedAnswers()) {
                // Validate question exists and belongs to the test
                Question question = questionRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(() -> new TestSubmissionException(
                                "Question with ID " + answerDTO.getQuestionId() + " not found"));

                if (!testQuestions.contains(question)) {
                    throw new TestSubmissionException(
                            "Question with ID " + answerDTO.getQuestionId()
                                    + " does not belong to the selected test parts");
                }

                // Skip part instruction questions
                if (question.getType().getName().equals("Part Instruction")) {
                    continue;
                }

                // Validate answer based on question type
                validateAnswerForQuestionType(question, answerDTO);

                SubmittedAnswer answer = new SubmittedAnswer();
                answer.setQuestion(question);
                answer.setWrittenAnswer(answerDTO.getWrittenAnswer());
                if (answerDTO.getSelectedOptions() != null) {
                    answer.setSelectedOptionIds(answerDTO.getSelectedOptions().stream()
                            .map(SelectedOptionDTO::getOptionId)
                            .collect(Collectors.toList()));
                }

                // Calculate if the answer is correct based on question type
                boolean isCorrect = calculateAnswerCorrectness(question, answerDTO);
                answer.setIsCorrect(isCorrect);

                tempSubmission.addSubmittedAnswer(answer);
            }
        }

        // Save to database
        try {
            TestSubmission savedSubmission = testSubmissionRepository.save(tempSubmission);
            return convertToDetailedResponseDTO(savedSubmission);
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to save test submission", e);
        }
    }

    @Override
    public TestSubmissionResponseDTO getSubmissionById(Integer id, Long userId) {
        if (id == null) {
            throw new TestSubmissionException("Submission ID cannot be null");
        }

        TestSubmission submission = testSubmissionRepository.findById(id)
                .orElseThrow(() -> new TestSubmissionException("Submission with ID " + id + " not found"));

        // Check if the submission belongs to the user
        if (!submission.getUserId().equals(userId)) {
            throw new TestSubmissionException("You don't have permission to view this submission");
        }

        return convertToDetailedResponseDTO(submission);
    }

    @Override
    public Page<TestSubmissionResponseDTO> getSubmissions(Long userId, Integer testId, Pageable pageable) {
        if (pageable == null) {
            throw new TestSubmissionException("Pageable parameter cannot be null");
        }

        try {
            Page<TestSubmission> submissions;
            if (testId != null) {
                submissions = testSubmissionRepository.findByUserIdAndTestId(userId, testId, pageable);
            } else {
                submissions = testSubmissionRepository.findByUserId(userId, pageable);
            }
            return submissions.map(this::convertToResponseDTO);
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to retrieve submissions", e);
        }
    }

    @Override
    @Transactional
    public void deleteSubmission(Integer id, Long userId) {
        if (id == null) {
            throw new TestSubmissionException("Submission ID cannot be null");
        }

        TestSubmission submission = testSubmissionRepository.findById(id)
                .orElseThrow(() -> new TestSubmissionException("Submission with ID " + id + " not found"));

        // Check if the submission belongs to the user
        if (!submission.getUserId().equals(userId)) {
            throw new TestSubmissionException("You don't have permission to delete this submission");
        }

        try {
            // Clear the submission parts collection first
            submission.getSubmissionParts().clear();

            // The submitted answers will be automatically deleted due to orphanRemoval =
            // true
            // The submission parts will be automatically deleted due to orphanRemoval =
            // true
            testSubmissionRepository.delete(submission);
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to delete submission", e);
        }
    }

    @Override
    public TestSubmissionResponseDTO evaluateSubmission(TestSubmissionRequestDTO requestDTO) {
        // Validate test exists
        Test test = testRepository.findById(requestDTO.getTestId())
                .orElseThrow(
                        () -> new TestSubmissionException("Test with ID " + requestDTO.getTestId() + " not found"));

        // Validate submission time
        if (requestDTO.getSubmittedAt() == null) {
            requestDTO.setSubmittedAt(LocalDateTime.now());
        }

        // Validate time spent
        if (requestDTO.getTimeSpent() == null || requestDTO.getTimeSpent() < 0) {
            throw new TestSubmissionException("Time spent must be a positive number");
        }

        // Get questions from selected test parts
        Set<Question> testQuestions;
        Set<TestPart> selectedParts;
        if (requestDTO.getPartIds() != null && !requestDTO.getPartIds().isEmpty()) {
            // Get questions only from selected parts
            selectedParts = test.getTestParts().stream()
                    .filter(part -> requestDTO.getPartIds().contains(part.getId()))
                    .collect(Collectors.toSet());

            if (selectedParts.isEmpty()) {
                throw new TestSubmissionException("No valid test parts selected");
            }

            testQuestions = selectedParts.stream()
                    .flatMap(part -> part.getQuestions().stream())
                    .collect(Collectors.toSet());

            if (testQuestions.isEmpty()) {
                throw new TestSubmissionException("No questions found in the selected test parts");
            }
        } else {
            // Get all questions if no parts specified
            selectedParts = new HashSet<>(test.getTestParts());
            testQuestions = selectedParts.stream()
                    .flatMap(part -> part.getQuestions().stream())
                    .collect(Collectors.toSet());

            if (testQuestions.isEmpty()) {
                throw new TestSubmissionException("Test has no questions");
            }
        }

        // Create a temporary submission object for evaluation only
        TestSubmission tempSubmission = new TestSubmission();
        tempSubmission.setTest(test);
        tempSubmission.setSubmittedAt(requestDTO.getSubmittedAt());
        tempSubmission.setStatus("EVALUATED");
        tempSubmission.setTimeSpent(requestDTO.getTimeSpent());

        // Add selected parts to submission
        for (TestPart part : selectedParts) {
            TestSubmissionPart submissionPart = new TestSubmissionPart();
            submissionPart.setPart(part);
            tempSubmission.addSubmissionPart(submissionPart);
        }

        // Process submitted answers if any
        if (requestDTO.getSubmittedAnswers() != null && !requestDTO.getSubmittedAnswers().isEmpty()) {
            for (SubmittedAnswerRequestDTO answerDTO : requestDTO.getSubmittedAnswers()) {
                // Validate question exists and belongs to the test
                Question question = questionRepository.findById(answerDTO.getQuestionId())
                        .orElseThrow(() -> new TestSubmissionException(
                                "Question with ID " + answerDTO.getQuestionId() + " not found"));

                if (!testQuestions.contains(question)) {
                    throw new TestSubmissionException(
                            "Question with ID " + answerDTO.getQuestionId()
                                    + " does not belong to the selected test parts");
                }

                // Skip part instruction questions
                if (question.getType().getName().equals("Part Instruction")) {
                    continue;
                }

                // Validate answer based on question type
                validateAnswerForQuestionType(question, answerDTO);

                SubmittedAnswer answer = new SubmittedAnswer();
                answer.setQuestion(question);
                answer.setWrittenAnswer(answerDTO.getWrittenAnswer());
                if (answerDTO.getSelectedOptions() != null) {
                    answer.setSelectedOptionIds(answerDTO.getSelectedOptions().stream()
                            .map(SelectedOptionDTO::getOptionId)
                            .collect(Collectors.toList()));
                }

                // Calculate if the answer is correct based on question type
                boolean isCorrect = calculateAnswerCorrectness(question, answerDTO);
                answer.setIsCorrect(isCorrect);

                tempSubmission.addSubmittedAnswer(answer);
            }
        }

        // Just return the evaluation results without saving
        return convertToDetailedResponseDTO(tempSubmission);
    }

    private void validateAnswerForQuestionType(Question question, SubmittedAnswerRequestDTO answerDTO) {
        QuestionType questionType = question.getType();
        String typeName = questionType.getName();

        switch (typeName) {
            case "Single Choice":
                validateSingleChoiceAnswer(answerDTO);
                break;
            case "Multiple Choice":
                validateMultipleChoiceAnswer(answerDTO);
                break;
            case "Fill in Blank":
                validateFillInBlankAnswer(answerDTO);
                break;
            case "Part Instruction":
                validatePartInstructionAnswer(answerDTO);
                break;
            default:
                throw new TestSubmissionException("Unknown question type: " + typeName);
        }
    }

    private void validateSingleChoiceAnswer(SubmittedAnswerRequestDTO answerDTO) {
        if (answerDTO.getSelectedOptions() == null || answerDTO.getSelectedOptions().size() != 1) {
            throw new TestSubmissionException("Single choice questions require exactly one selected option");
        }
        if (answerDTO.getWrittenAnswer() != null && !answerDTO.getWrittenAnswer().trim().isEmpty()) {
            throw new TestSubmissionException("Single choice questions should not have a written answer");
        }
    }

    private void validateMultipleChoiceAnswer(SubmittedAnswerRequestDTO answerDTO) {
        if (answerDTO.getSelectedOptions() == null || answerDTO.getSelectedOptions().isEmpty()) {
            throw new TestSubmissionException("Multiple choice questions require at least one selected option");
        }
        if (answerDTO.getWrittenAnswer() != null && !answerDTO.getWrittenAnswer().trim().isEmpty()) {
            throw new TestSubmissionException("Multiple choice questions should not have a written answer");
        }

        // Check for duplicate option IDs
        Set<String> uniqueOptionIds = new HashSet<>();
        for (SelectedOptionDTO option : answerDTO.getSelectedOptions()) {
            if (!uniqueOptionIds.add(option.getOptionId())) {
                throw new TestSubmissionException("Duplicate option ID found: " + option.getOptionId());
            }
        }
    }

    private void validateFillInBlankAnswer(SubmittedAnswerRequestDTO answerDTO) {
        if (answerDTO.getWrittenAnswer() == null || answerDTO.getWrittenAnswer().trim().isEmpty()) {
            throw new TestSubmissionException("Fill in blank questions require a written answer");
        }
        if (answerDTO.getSelectedOptions() != null && !answerDTO.getSelectedOptions().isEmpty()) {
            throw new TestSubmissionException("Fill in blank questions should not have selected options");
        }
    }

    private void validatePartInstructionAnswer(SubmittedAnswerRequestDTO answerDTO) {
        // Part instruction questions don't require any answer
        // They are just instructional text
        // Allow both writtenAnswer and selectedOptions to be null or empty
        if (answerDTO.getWrittenAnswer() != null && !answerDTO.getWrittenAnswer().trim().isEmpty() ||
                answerDTO.getSelectedOptions() != null && !answerDTO.getSelectedOptions().isEmpty()) {
            throw new TestSubmissionException("Part instruction questions should not have any answers");
        }
    }

    private boolean calculateAnswerCorrectness(Question question, SubmittedAnswerRequestDTO answerDTO) {
        QuestionType questionType = question.getType();
        String typeName = questionType.getName();

        switch (typeName) {
            case "Single Choice":
            case "Multiple Choice":
                return checkMultipleChoiceCorrectness(question, answerDTO.getSelectedOptions());
            case "Fill in Blank":
                return checkWrittenAnswerCorrectness(question, answerDTO.getWrittenAnswer());
            case "Part Instruction":
                return true; // Part instruction questions are always considered correct
            default:
                return false;
        }
    }

    private boolean checkMultipleChoiceCorrectness(Question question, List<SelectedOptionDTO> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return false;
        }

        String correctAnswer = question.getCorrectAnswer();
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            return false;
        }

        // Check for duplicate option IDs
        Set<String> selectedOptionIds = new HashSet<>();
        for (SelectedOptionDTO option : selectedOptions) {
            if (!selectedOptionIds.add(option.getOptionId())) {
                return false; // Return false if duplicates are found
            }
        }

        // For single choice questions, correctAnswer is a single letter
        if (question.getType().getName().equals("Single Choice")) {
            return selectedOptionIds.size() == 1 &&
                    selectedOptionIds.contains(correctAnswer.trim());
        }

        // For multiple choice questions, correctAnswer is comma-separated
        Set<String> correctOptionIds = Arrays.stream(correctAnswer.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        return selectedOptionIds.equals(correctOptionIds);
    }

    private boolean checkWrittenAnswerCorrectness(Question question, String writtenAnswer) {
        if (writtenAnswer == null || writtenAnswer.trim().isEmpty()) {
            return false;
        }

        String correctAnswer = question.getCorrectAnswer();
        if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
            return false;
        }

        // For written answers, we do a case-insensitive comparison
        // and trim whitespace from both answers
        return writtenAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }

    private TestSubmissionResponseDTO convertToDetailedResponseDTO(TestSubmission submission) {
        TestSubmissionResponseDTO dto = new TestSubmissionResponseDTO();
        dto.setId(submission.getId());
        dto.setTestId(submission.getTest().getId());
        dto.setUserId(submission.getUserId());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setStatus(submission.getStatus());
        dto.setTimeSpent(submission.getTimeSpent());

        // Add test information
        Test test = submission.getTest();
        dto.setTestTypeId(test.getType().getId());
        dto.setTestTypeName(test.getType().getName());
        dto.setTestTitle(test.getTitle());
        dto.setInstructorName(test.getInstructorName());

        // Calculate total statistics
        int totalCorrect = 0;
        int totalQuestions = 0;

        // Group answers by part
        Map<TestPart, List<SubmittedAnswer>> answersByPart = submission.getSubmittedAnswers().stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getPart()));

        List<TestPartSubmissionDTO> partSubmissions = new ArrayList<>();

        // Process each part
        for (TestSubmissionPart submissionPart : submission.getSubmissionParts()) {
            TestPart part = submissionPart.getPart();
            List<SubmittedAnswer> partAnswers = answersByPart.getOrDefault(part, new ArrayList<>());

            TestPartSubmissionDTO partDTO = new TestPartSubmissionDTO();
            partDTO.setPartId(part.getId());
            partDTO.setPartName(part.getName());
            partDTO.setPartDescription(part.getDescription());
            partDTO.setPartDuration(part.getDuration());
            partDTO.setPartIcon(part.getIcon());
            partDTO.setPartOrder(part.getOrder());
            partDTO.setAudioUrl(part.getAudioUrl());

            // Get all questions for this part
            List<Question> partQuestions = part.getQuestions();
            List<SubmittedAnswerResponseDTO> answerDTOs = new ArrayList<>();

            // Create a map of submitted answers for quick lookup
            Map<Integer, SubmittedAnswer> submittedAnswersMap = partAnswers.stream()
                    .collect(Collectors.toMap(
                            answer -> answer.getQuestion().getId(),
                            answer -> answer));

            // Process all questions in the part
            for (Question question : partQuestions) {
                // Remove the instruction part question exclusion here
                SubmittedAnswerResponseDTO answerDTO = new SubmittedAnswerResponseDTO();
                answerDTO.setQuestionId(question.getId());
                answerDTO.setQuestionTitle(question.getTitle());
                answerDTO.setQuestionInstruction(question.getQuestionInstruction());
                answerDTO.setAnswerInstruction(question.getAnswerInstruction());
                answerDTO.setImageUrl(question.getImageUrl());
                answerDTO.setReadingPassage(question.getReadingPassage());
                answerDTO.setQuestionType(question.getType().getName());
                answerDTO.setOrder(question.getOrder());

                // Add question options
                answerDTO.setQuestionOptions(
                        question.getQuestionOptions() != null ? question.getQuestionOptions().stream()
                                .map(option -> {
                                    QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                                    optionDTO.setId(option.getId());
                                    optionDTO.setOptionId(option.getOptionId());
                                    optionDTO.setText(option.getText());
                                    return optionDTO;
                                })
                                .collect(Collectors.toList()) : new ArrayList<>());

                // If there's a submitted answer for this question, use it
                SubmittedAnswer submittedAnswer = submittedAnswersMap.get(question.getId());
                if (submittedAnswer != null) {
                    answerDTO.setId(submittedAnswer.getId());
                    answerDTO.setWrittenAnswer(submittedAnswer.getWrittenAnswer());
                    answerDTO.setIsCorrect(submittedAnswer.getIsCorrect());
                    answerDTO.setSelectedOptionIds(submittedAnswer.getSelectedOptionIds());
                } else {
                    // For unanswered questions, set default values
                    answerDTO.setWrittenAnswer(null);
                    answerDTO.setIsCorrect(false);
                    answerDTO.setSelectedOptionIds(null);
                }

                // Always include the correct answer
                String typeName = question.getType().getName();
                if (typeName.equals("Multiple Choice")) {
                    // For multiple choice, return the list of correct option IDs
                    answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                    answerDTO.setOrder(question.getOrder());
                } else if (typeName.equals("Single Choice")) {
                    // For single choice, return the single correct option ID
                    answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                    answerDTO.setOrder(question.getOrder());
                } else {
                    // For other types (like Fill in Blank), return the correct answer as is
                    answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                    answerDTO.setOrder(question.getOrder());
                }

                answerDTOs.add(answerDTO);
            }

            // Calculate part statistics - keep the instruction part exclusion here
            int partCorrect = (int) partAnswers.stream()
                    .filter(answer -> !answer.getQuestion().getType().getName().equals("Part Instruction"))
                    .filter(SubmittedAnswer::getIsCorrect)
                    .count();
            int partTotal = (int) partQuestions.stream()
                    .filter(question -> !question.getType().getName().equals("Part Instruction"))
                    .count();
            partDTO.setCorrectAnswers(partCorrect);
            partDTO.setTotalQuestions(partTotal);
            partDTO.setPartScore(partTotal > 0 ? Math.round((double) partCorrect / partTotal * 100.0) / 100.0 : 0.0);

            // Always include answers for detailed view
            partDTO.setAnswers(answerDTOs);

            totalCorrect += partCorrect;
            totalQuestions += partTotal;

            partSubmissions.add(partDTO);
        }

        dto.setTotalCorrectAnswers(totalCorrect);
        dto.setTotalQuestions(totalQuestions);
        dto.setPartSubmissions(partSubmissions);

        return dto;
    }

    private TestSubmissionResponseDTO convertToResponseDTO(TestSubmission submission) {
        TestSubmissionResponseDTO dto = new TestSubmissionResponseDTO();
        dto.setId(submission.getId());
        dto.setTestId(submission.getTest().getId());
        dto.setUserId(submission.getUserId());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setStatus(submission.getStatus());
        dto.setTimeSpent(submission.getTimeSpent());

        // Add test information
        Test test = submission.getTest();
        dto.setTestTypeId(test.getType().getId());
        dto.setTestTypeName(test.getType().getName());
        dto.setTestTitle(test.getTitle());
        dto.setInstructorName(test.getInstructorName());

        // Calculate total statistics
        int totalCorrect = 0;
        int totalQuestions = 0;

        // Group answers by part
        Map<TestPart, List<SubmittedAnswer>> answersByPart = submission.getSubmittedAnswers().stream()
                .collect(Collectors.groupingBy(answer -> answer.getQuestion().getPart()));

        List<TestPartSubmissionDTO> partSubmissions = new ArrayList<>();

        // Process each part
        for (TestSubmissionPart submissionPart : submission.getSubmissionParts()) {
            TestPart part = submissionPart.getPart();
            List<SubmittedAnswer> partAnswers = answersByPart.getOrDefault(part, new ArrayList<>());

            TestPartSubmissionDTO partDTO = new TestPartSubmissionDTO();
            partDTO.setPartId(part.getId());
            partDTO.setPartName(part.getName());
            partDTO.setPartDescription(part.getDescription());
            partDTO.setPartDuration(part.getDuration());
            partDTO.setPartIcon(part.getIcon());
            partDTO.setPartOrder(part.getOrder());
            partDTO.setAudioUrl(part.getAudioUrl());

            // Get all questions for this part
            List<Question> partQuestions = part.getQuestions();

            // Only include detailed answers if flag is set
            if (dto.isIncludeDetailedAnswers()) {
                List<SubmittedAnswerResponseDTO> answerDTOs = new ArrayList<>();

                // Create a map of submitted answers for quick lookup
                Map<Integer, SubmittedAnswer> submittedAnswersMap = partAnswers.stream()
                        .collect(Collectors.toMap(
                                answer -> answer.getQuestion().getId(),
                                answer -> answer));

                // Process all questions in the part
                for (Question question : partQuestions) {
                    // Remove the instruction part question exclusion here
                    SubmittedAnswerResponseDTO answerDTO = new SubmittedAnswerResponseDTO();
                    answerDTO.setQuestionId(question.getId());
                    answerDTO.setQuestionTitle(question.getTitle());
                    answerDTO.setQuestionInstruction(question.getQuestionInstruction());
                    answerDTO.setAnswerInstruction(question.getAnswerInstruction());
                    answerDTO.setImageUrl(question.getImageUrl());
                    answerDTO.setReadingPassage(question.getReadingPassage());
                    answerDTO.setQuestionType(question.getType().getName());
                    answerDTO.setOrder(question.getOrder());

                    // Add question options
                    answerDTO.setQuestionOptions(
                            question.getQuestionOptions() != null ? question.getQuestionOptions().stream()
                                    .map(option -> {
                                        QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                                        optionDTO.setId(option.getId());
                                        optionDTO.setOptionId(option.getOptionId());
                                        optionDTO.setText(option.getText());
                                        return optionDTO;
                                    })
                                    .collect(Collectors.toList()) : new ArrayList<>());

                    // If there's a submitted answer for this question, use it
                    SubmittedAnswer submittedAnswer = submittedAnswersMap.get(question.getId());
                    if (submittedAnswer != null) {
                        answerDTO.setId(submittedAnswer.getId());
                        answerDTO.setWrittenAnswer(submittedAnswer.getWrittenAnswer());
                        answerDTO.setIsCorrect(submittedAnswer.getIsCorrect());
                        answerDTO.setSelectedOptionIds(submittedAnswer.getSelectedOptionIds());
                    } else {
                        // For unanswered questions, set default values
                        answerDTO.setWrittenAnswer(null);
                        answerDTO.setIsCorrect(false);
                        answerDTO.setSelectedOptionIds(null);
                    }

                    // Always include the correct answer
                    String typeName = question.getType().getName();
                    if (typeName.equals("Multiple Choice")) {
                        // For multiple choice, return the list of correct option IDs
                        answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                        answerDTO.setOrder(question.getOrder());
                    } else if (typeName.equals("Single Choice")) {
                        // For single choice, return the single correct option ID
                        answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                        answerDTO.setOrder(question.getOrder());
                    } else {
                        // For other types (like Fill in Blank), return the correct answer as is
                        answerDTO.setCorrectAnswer(question.getCorrectAnswer());
                        answerDTO.setOrder(question.getOrder());
                    }

                    // Add question options
                    answerDTO.setQuestionOptions(
                            question.getQuestionOptions() != null ? question.getQuestionOptions().stream()
                                    .map(option -> {
                                        QuestionOptionDTO optionDTO = new QuestionOptionDTO();
                                        optionDTO.setId(option.getId());
                                        optionDTO.setOptionId(option.getOptionId());
                                        optionDTO.setText(option.getText());
                                        return optionDTO;
                                    })
                                    .collect(Collectors.toList()) : new ArrayList<>());

                    answerDTOs.add(answerDTO);
                }
                partDTO.setAnswers(answerDTOs);
            }

            // Calculate part statistics - keep the instruction part exclusion here
            int partCorrect = (int) partAnswers.stream()
                    .filter(answer -> !answer.getQuestion().getType().getName().equals("Part Instruction"))
                    .filter(SubmittedAnswer::getIsCorrect)
                    .count();
            int partTotal = (int) partQuestions.stream()
                    .filter(question -> !question.getType().getName().equals("Part Instruction"))
                    .count();
            partDTO.setCorrectAnswers(partCorrect);
            partDTO.setTotalQuestions(partTotal);
            partDTO.setPartScore(partTotal > 0 ? Math.round((double) partCorrect / partTotal * 100.0) / 100.0 : 0.0);

            totalCorrect += partCorrect;
            totalQuestions += partTotal;

            partSubmissions.add(partDTO);
        }

        dto.setTotalCorrectAnswers(totalCorrect);
        dto.setTotalQuestions(totalQuestions);
        dto.setPartSubmissions(partSubmissions);

        return dto;
    }
}