package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.TestLevelDTO;
import swp.group2.swpbe.exam.service.TestLevelService;
import java.util.List;

@RestController
@RequestMapping("/api/test-levels")
@CrossOrigin(origins = "*")
public class TestLevelController {

    @Autowired
    private TestLevelService testLevelService;

    @GetMapping
    public ResponseEntity<List<TestLevelDTO>> getAllTestLevels() {
        return ResponseEntity.ok(testLevelService.getAllTestLevels());
    }
}