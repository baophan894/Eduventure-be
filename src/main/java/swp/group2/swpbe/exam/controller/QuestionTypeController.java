package swp.group2.swpbe.exam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.group2.swpbe.exam.dto.QuestionTypeDTO;
import swp.group2.swpbe.exam.service.QuestionTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/question-types")
@CrossOrigin(origins = "*")
public class QuestionTypeController {

    @Autowired
    private QuestionTypeService questionTypeService;

    @GetMapping
    public ResponseEntity<List<QuestionTypeDTO>> getAllQuestionTypes() {
        List<QuestionTypeDTO> questionTypes = questionTypeService.getAllQuestionTypes();
        return ResponseEntity.ok(questionTypes);
    }
}