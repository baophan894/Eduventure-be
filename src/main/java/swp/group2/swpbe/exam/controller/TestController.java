package swp.group2.swpbe.exam.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import swp.group2.swpbe.exam.dto.TestDTO;
import swp.group2.swpbe.exam.dto.TestResponseDTO;
import swp.group2.swpbe.exam.service.TestService;
import swp.group2.swpbe.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.Map;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<TestDTO> createTest(@RequestBody TestDTO testDTO) {
        return ResponseEntity.ok(testService.createTest(testDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTest(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(testService.getTest(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Test with id " + id + " not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTest(
            @PathVariable Integer id,
            @RequestPart(value = "test", required = true) TestDTO testDTO,
            @RequestParam(value = "coverImg", required = false) MultipartFile coverImg,
            @RequestParam(value = "instructorAvatar", required = false) MultipartFile instructorAvatar,
            @RequestParam Map<String, MultipartFile> files) {
        try {
            if (coverImg != null && !coverImg.isEmpty()) {
                Map data = cloudinaryService.upload(coverImg);
                testDTO.setCoverImg((String) data.get("url"));
            }

            if (instructorAvatar != null && !instructorAvatar.isEmpty()) {
                Map data = cloudinaryService.upload(instructorAvatar);
                testDTO.setInstructorAvatar((String) data.get("url"));
            }

            // Handle all files
            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                String key = entry.getKey();
                MultipartFile file = entry.getValue();

                // Skip coverImg and instructorAvatar as they are handled separately
                if (key.equals("coverImg") || key.equals("instructorAvatar")) {
                    continue;
                }

                if (file != null && !file.isEmpty()) {
                    Map data = cloudinaryService.upload(file);
                    String url = (String) data.get("url");

                    // Handle question images with new format: part_[part order]_question_[question
                    // order]_image
                    if (key.startsWith("part_") && key.endsWith("_image")) {
                        String[] parts = key.split("_");
                        if (parts.length == 5) { // part_[order]_question_[order]_image
                            try {
                                int partOrder = Integer.parseInt(parts[1]);
                                int questionOrder = Integer.parseInt(parts[3]);

                                if (testDTO.getTestParts() != null) {
                                    var part = testDTO.getTestParts().stream()
                                            .filter(p -> p.getOrder() == partOrder)
                                            .findFirst()
                                            .orElse(null);

                                    if (part != null && part.getQuestions() != null) {
                                        var question = part.getQuestions().stream()
                                                .filter(q -> q.getOrder() == questionOrder)
                                                .findFirst()
                                                .orElse(null);

                                        if (question != null && question.getImageUrl() != null
                                                && question.getImageUrl().startsWith("blob:")) {
                                            question.setImageUrl(url);
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                logger.error("Invalid order format in key: {}", key);
                            }
                        }
                    }
                    // Handle part audio
                    else if (key.startsWith("part_") && key.endsWith("_audio")) {
                        String[] parts = key.split("_");
                        if (parts.length == 3) { // part_[order]_audio
                            try {
                                int partOrder = Integer.parseInt(parts[1]);

                                if (testDTO.getTestParts() != null) {
                                    var part = testDTO.getTestParts().stream()
                                            .filter(p -> p.getOrder() == partOrder)
                                            .findFirst()
                                            .orElse(null);

                                    if (part != null && part.getAudioUrl() != null
                                            && part.getAudioUrl().startsWith("blob:")) {
                                        part.setAudioUrl(url);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                logger.error("Invalid order format in key: {}", key);
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok(testService.updateTest(id, testDTO));
        } catch (EntityNotFoundException e) {
            logger.error("Test not found with id: {}", id);
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Test with id " + id + " not found"));
        } catch (Exception e) {
            logger.error("Error processing test update: ", e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "status", 500,
                            "error", "Internal Server Error",
                            "message", "An error occurred while processing the request"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTest(@PathVariable Integer id) {
        try {
            testService.deleteTest(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404)
                    .body(Map.of(
                            "status", 404,
                            "error", "Not Found",
                            "message", "Test with id " + id + " not found"));
        }
    }

    @GetMapping
    public ResponseEntity<TestResponseDTO> getAllTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer testLevelId,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) Integer languageId) {
        TestResponseDTO response = testService.getAllTests(page, size, search, testLevelId, typeId, languageId);
        return ResponseEntity.ok(response);
    }
}