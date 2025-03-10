package swp.group2.swpbe.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import swp.group2.swpbe.AuthService;
import swp.group2.swpbe.CloudinaryService;
import swp.group2.swpbe.JwtTokenProvider;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.user.dto.ChangePasswordDTO;
import swp.group2.swpbe.user.dto.LoginDTO;
import swp.group2.swpbe.user.dto.LoginSocialDTO;
import swp.group2.swpbe.user.dto.ResetPasswordDTO;
import swp.group2.swpbe.user.dto.ReverifyDTO;
import swp.group2.swpbe.user.dto.SignupDTO;
import swp.group2.swpbe.user.dto.UpdatePasswordDTO;
import swp.group2.swpbe.user.dto.UpdateProfileDTO;
import swp.group2.swpbe.user.entities.User;
import swp.group2.swpbe.user.entities.Withdraw;
import swp.group2.swpbe.user.response.LoginResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    AuthService authService;

    @Value("${allow.origin}")
    private String allowedOrigins;

    @PostMapping("/signup")
    public User create(@RequestBody SignupDTO body) {
        return userService.signup(body);
    }

    @PostMapping("/social")
    public LoginResponse loginSocial(@RequestBody LoginSocialDTO body) {
        User user = userService.saveSocialUser(body);
        String token = jwtTokenProvider.generateAccessToken(user.getId() + "");
        return new LoginResponse(token, user.getRole());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO body) {
        userService.resetPassword(body);
        return ResponseEntity.ok("Email sent successfully.");

    }

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam(name = "token") String query, HttpServletResponse response) {
        userService.updateVerifyEmail(query);
        try {
            response.sendRedirect(allowedOrigins + "/login");
        } catch (Exception e) {
            throw new ApiRequestException("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/reverify")
    public ResponseEntity<?> reverify(@RequestBody ReverifyDTO body) {
        userService.reverify(body.getEmail());
        return ResponseEntity.ok("Reverification email sent successfully.");

    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginDTO body) {
        User user = userService.login(body);
        String token = jwtTokenProvider.generateAccessToken(user.getId() + "");
        return new LoginResponse(token, user.getRole());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO body) {
        userService.changePassword(body);
        return ResponseEntity.ok("Reset password successfully.");

    }

    @GetMapping("/profile")
    public User getProfile(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return userService.getUserProfile(userId);
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO body,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        userService.updatePassword(body, userId);
        return ResponseEntity.ok("Update password successfully");
    }

    @PatchMapping("/update-avatar")
    public ResponseEntity<?> update(@RequestParam("image") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        Map data = this.cloudinaryService.upload(file);
        String url = (String) data.get("url");
        userService.updateAvatar(url, userId);
        return new ResponseEntity<>("update avatar successfully", HttpStatus.OK);
    }

    @PutMapping("user/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDTO body,
            @RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        userService.updateProfile(body, userId);
        return new ResponseEntity<>("update profile successfully", HttpStatus.OK);
    }

    @GetMapping("public/experts")
    public List<User> getAllExpert() {
        return userService.getAllExpert();
    }

    @GetMapping("public/users")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("withdraw/histories")
    public List<Withdraw> withdrawHistories(@RequestHeader("Authorization") String token) {
        String userId = authService.loginUser(token);
        return userService.listWithdraws(userId);
    }

    @GetMapping("withdraw/all-histories")
    public List<Withdraw> allWithdrawHistories() {
        return userService.listAllWithdraws();
    }

    @PutMapping("user/state/{id}")
    public ResponseEntity<?> putMethodName(@PathVariable int id, @RequestHeader("Authorization") String token,
            @RequestBody int state) {
        String userId = authService.loginUser(token);
        if (!authService.isAdmin(userId)) {
            throw new ApiRequestException("FORBIDDEN", HttpStatus.FORBIDDEN);
        }
        userService.updateUserState(id, state);
        return new ResponseEntity<>("update state successfully", HttpStatus.OK);
    }
}