package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.TestTypeDTO;
import swp.group2.swpbe.exam.service.TestTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/test-types")
@CrossOrigin(origins = "*")
public class TestTypeController {

    @Autowired
    private TestTypeService testTypeService;

    @GetMapping
    public ResponseEntity<List<TestTypeDTO>> getAllTestTypes() {
        List<TestTypeDTO> testTypes = testTypeService.getAllTestTypes();
        return ResponseEntity.ok(testTypes);
    }
}