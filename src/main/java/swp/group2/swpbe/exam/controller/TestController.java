package swp.group2.swpbe.exam.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.TestDTO;
import swp.group2.swpbe.exam.dto.TestResponseDTO;
import swp.group2.swpbe.exam.service.TestService;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping
    public ResponseEntity<TestDTO> createTest(@RequestBody TestDTO testDTO) {
        return ResponseEntity.ok(testService.createTest(testDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestDTO> getTest(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(testService.getTest(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestDTO> updateTest(@PathVariable Integer id, @RequestBody TestDTO testDTO) {
        try {
            return ResponseEntity.ok(testService.updateTest(id, testDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Integer id) {
        try {
            testService.deleteTest(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<TestResponseDTO> getAllTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String search) {
        TestResponseDTO response = testService.getAllTests(page, size, search);
        return ResponseEntity.ok(response);
    }
}