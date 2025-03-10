package swp.group2.swpbe.topic;

import org.springframework.web.bind.annotation.RestController;

import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.document.entities.Document;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.topic.DTO.UpdateTopicDTO;
import swp.group2.swpbe.topic.enties.Topic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TopicController {
    @Autowired
    TopicService topicService;
    @Autowired
    AuthService authService;

    @GetMapping("topics")
    public List<Topic> getAllTopics() {
        return topicService.getAllTopic();
    }

    @PostMapping("topic")
    public Topic createTopic(@RequestHeader("Authorization") String token, @RequestParam String name) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        System.out.println(name);
        return topicService.createTopic(name);
        
    }

    @PutMapping("topic/{id}")
    public Topic updateTopic(@RequestHeader("Authorization") String token, @PathVariable int id,
            @RequestBody UpdateTopicDTO body) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        return topicService.updateTopic(id, body.getNewName());
    }

    @DeleteMapping("topic/{id}")
    public ResponseEntity<?> deleteTopic(@RequestHeader("Authorization") String token, @PathVariable int id) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        topicService.deleteTopic(id);
        return new ResponseEntity<>("Document state updated to pending successfully", HttpStatus.OK);
    }

}
