package swp.group2.swpbe.flashcard;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.flashcard.dto.FlashcardDTO;
import swp.group2.swpbe.flashcard.dto.FlashcardQuestionDTO;
import swp.group2.swpbe.flashcard.entities.Flashcard;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class FlashcardController {
    @Autowired
    FlashcardService flashcardService;
    @Autowired
    AuthService authService;

    @PostMapping("/flashcard/create")
    public Flashcard createFlashcard(@RequestBody FlashcardDTO body, @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return flashcardService.create(body, userId);
    }

    @PutMapping("flashcard/update/{id}")
    public Flashcard updateFlashcard(@RequestBody FlashcardDTO body, @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return flashcardService.update(body, id, userId);
    }

    // get flashcard
    @GetMapping("/flashcards")
    public List<Flashcard> getAllFlashcards() {
        return flashcardService.getAllFlashCard();
    }

    @PostMapping("/flashcard/upload-review")
    public ResponseEntity<?> review(@RequestParam("flashcardId") String flashcardId,
            @RequestParam("review") String review,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);

        flashcardService.uploadReviewFlashcard(flashcardId, userId, ReviewState.valueOf(review));
        return new ResponseEntity<>("upload review successfully", HttpStatus.OK);
    }

    @PutMapping("/flashcard/acitve")
    public ResponseEntity<?> setFlashcardToActive(@RequestParam("flashcardId") String flashcardId,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        Flashcard flashcard = flashcardService.getFlashcardById(Integer.parseInt(flashcardId));

        if (flashcard == null) {
            return new ResponseEntity<>("Flashcard not found", HttpStatus.NOT_FOUND);
        }

        flashcardService.updateFlashcardState(flashcard, ResourceStatus.active);
        return new ResponseEntity<>("Flashcard state updated to pending successfully", HttpStatus.OK);
    }

    @PostMapping("/flashcard/upload-csv")
    public ResponseEntity<?> createFlashcardFromCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("topicId") String topicId,
            @RequestParam("description") String description,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = authService.loginUser(token);

            // Đọc file CSV
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            List<FlashcardQuestionDTO> questions = new ArrayList<>();
            List<String[]> rows = csvReader.readAll();

            // Chuyển đổi dữ liệu từ CSV sang FlashcardQuestionDTO
            for (String[] row : rows) {
                if (row.length < 2)
                    continue; // Bỏ qua dòng không hợp lệ
                FlashcardQuestionDTO question = new FlashcardQuestionDTO(row[0], row[1]);
                questions.add(question);
            }

            // Tạo FlashcardDTO từ dữ liệu đọc được
            FlashcardDTO flashcardDTO = new FlashcardDTO(name, description, topicId, questions);

            // Lưu flashcard vào cơ sở dữ liệu
            Flashcard flashcard = flashcardService.create(flashcardDTO, userId);

            return ResponseEntity.ok(flashcard);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create flashcard from CSV: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }

}