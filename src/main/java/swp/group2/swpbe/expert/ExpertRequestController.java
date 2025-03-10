package swp.group2.swpbe.expert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.expert.entities.ExpertRequest;
import swp.group2.swpbe.user.UserService;
import java.util.List;

@RestController
public class ExpertRequestController {
    @Autowired
    private ExpertRequestService expertRequestService;
    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;


    @PostMapping("expert-request")
    public ResponseEntity<?> createExpertRequest(
            @RequestHeader("Authorization") String token,
            @RequestParam("cv") MultipartFile file) {
        String userId = authService.loginUser(token);
        boolean isAllowRequest = userService.getUserLegit(userId) > 1;
        if (isAllowRequest) {
            expertRequestService.saveRequest(userId, file);
        } else {
            throw new ApiRequestException("your legit is not enough to be expert", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("request sent successfully", HttpStatus.OK);
    }

    @GetMapping("/expert-requests/all")
    public List<ExpertRequest> getExpertRequest(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        return expertRequestService.getAllExpert();
    }

    @GetMapping("/expert-requests/my-request")
    public List<ExpertRequest> getExpertRequestbyUserId(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return expertRequestService.getAllExpertByUserId(userId);
    }

    @GetMapping("/expert-requests/own")
    public List<ExpertRequest> getOwnRequest(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return expertRequestService.getOwnExpertRequests(userId);
    }

    @PutMapping("expert-request/accept/{id}")
    public ResponseEntity<?> acceptExpertRequest(@RequestHeader("Authorization") String token, @PathVariable int id) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        expertRequestService.acceptExpertRequest(id);
        return new ResponseEntity<>("Successfully", HttpStatus.OK);

    }

    @PutMapping("expert-request/reject/{id}")
    public ResponseEntity<?> rejectExpertRequest(@RequestHeader("Authorization") String token, @PathVariable int id) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        expertRequestService.rejectExpertRequest(id);
        return new ResponseEntity<>("Successfully", HttpStatus.OK);

    }

}
