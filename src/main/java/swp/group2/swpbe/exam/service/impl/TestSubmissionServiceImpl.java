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
    public TestSubmissionResponseDTO createSubmission(TestSubmissionRequestDTO requestDTO) {
        // Validate test exists
        Test test = testRepository.findById(requestDTO.getTestId())
                .orElseThrow(
                        () -> new TestSubmissionException("Test with ID " + requestDTO.getTestId() + " not found"));

        // Validate submission time
        if (requestDTO.getSubmittedAt() == null) {
            requestDTO.setSubmittedAt(LocalDateTime.now());
        }

        // Validate user ID
        if (requestDTO.getUserId() == null) {
            throw new TestSubmissionException("User ID is required");
        }

        // Validate submitted answers
        if (requestDTO.getSubmittedAnswers() == null || requestDTO.getSubmittedAnswers().isEmpty()) {
            throw new TestSubmissionException("At least one answer must be submitted");
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

        TestSubmission submission = new TestSubmission();
        submission.setTest(test);
        submission.setUserId(requestDTO.getUserId());
        submission.setSubmittedAt(requestDTO.getSubmittedAt());
        submission.setStatus("SUBMITTED");

        // Add selected parts to submission
        for (TestPart part : selectedParts) {
            TestSubmissionPart submissionPart = new TestSubmissionPart();
            submissionPart.setPart(part);
            submission.addSubmissionPart(submissionPart);
        }

        // Process each submitted answer
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

            // Calculate if the answer is correct based on question type
            boolean isCorrect = calculateAnswerCorrectness(question, answerDTO);
            answer.setIsCorrect(isCorrect);

            submission.addSubmittedAnswer(answer);
        }

        try {
            TestSubmission savedSubmission = testSubmissionRepository.save(submission);
            return convertToResponseDTO(savedSubmission);
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to save test submission", e);
        }
    }

    @Override
    public TestSubmissionResponseDTO getSubmissionById(Integer id) {
        if (id == null) {
            throw new TestSubmissionException("Submission ID cannot be null");
        }

        TestSubmission submission = testSubmissionRepository.findById(id)
                .orElseThrow(() -> new TestSubmissionException("Submission with ID " + id + " not found"));
        return convertToResponseDTO(submission);
    }

    @Override
    public Page<TestSubmissionResponseDTO> getSubmissions(Long userId, Integer testId, Pageable pageable) {
        if (pageable == null) {
            throw new TestSubmissionException("Pageable parameter cannot be null");
        }

        try {
            Page<TestSubmission> submissions;
            if (userId != null && testId != null) {
                submissions = testSubmissionRepository.findByUserIdAndTestId(userId, testId, pageable);
            } else if (userId != null) {
                submissions = testSubmissionRepository.findByUserId(userId, pageable);
            } else if (testId != null) {
                submissions = testSubmissionRepository.findByTestId(testId, pageable);
            } else {
                submissions = testSubmissionRepository.findAll(pageable);
            }
            return submissions.map(this::convertToResponseDTO);
        } catch (Exception e) {
            throw new TestSubmissionException("Failed to retrieve submissions", e);
        }
    }

    @Override
    @Transactional
    public void deleteSubmission(Integer id) {
        if (id == null) {
            throw new TestSubmissionException("Submission ID cannot be null");
        }

        TestSubmission submission = testSubmissionRepository.findById(id)
                .orElseThrow(() -> new TestSubmissionException("Submission with ID " + id + " not found"));

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

    private TestSubmissionResponseDTO convertToResponseDTO(TestSubmission submission) {
        TestSubmissionResponseDTO dto = new TestSubmissionResponseDTO();
        dto.setId(submission.getId());
        dto.setTestId(submission.getTest().getId());
        dto.setUserId(submission.getUserId());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setStatus(submission.getStatus());

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

            // Calculate part statistics
            int partCorrect = (int) partAnswers.stream()
                    .filter(SubmittedAnswer::getIsCorrect)
                    .count();
            int partTotal = partAnswers.size();

            partDTO.setCorrectAnswers(partCorrect);
            partDTO.setTotalQuestions(partTotal);

            // Add answers for this part
            List<SubmittedAnswerResponseDTO> answerDTOs = partAnswers.stream()
                    .map(this::convertToAnswerResponseDTO)
                    .collect(Collectors.toList());
            partDTO.setAnswers(answerDTOs);

            partSubmissions.add(partDTO);

            // Update total statistics
            totalCorrect += partCorrect;
            totalQuestions += partTotal;
        }

        dto.setPartSubmissions(partSubmissions);
        dto.setTotalCorrectAnswers(totalCorrect);
        dto.setTotalQuestions(totalQuestions);

        return dto;
    }

    private SubmittedAnswerResponseDTO convertToAnswerResponseDTO(SubmittedAnswer answer) {
        SubmittedAnswerResponseDTO dto = new SubmittedAnswerResponseDTO();
        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setWrittenAnswer(answer.getWrittenAnswer());
        dto.setIsCorrect(answer.getIsCorrect());
        return dto;
    }
}