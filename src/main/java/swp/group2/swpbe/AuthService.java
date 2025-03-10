package swp.group2.swpbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import swp.group2.swpbe.constant.UserRole;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.user.UserRepository;
import swp.group2.swpbe.user.entities.User;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public String loginUser(String token) {
        if (token == null) {
            throw new ApiRequestException("invalid_request", HttpStatus.UNAUTHORIZED);
        }
        String userId = null;
        try {
            userId = jwtTokenProvider.verifyToken(token);
        } catch (ExpiredJwtException e) {
            throw new ApiRequestException("expired_session", HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }

    public boolean isExpert(String userId) {
        User user = userRepository.findById(Integer.parseInt(userId));
        return user.getRole().equals(UserRole.EXPERT);
    }

    public boolean isAdmin(String userId) {
        User user = userRepository.findById(Integer.parseInt(userId));
        return user.getRole().equals(UserRole.ADMIN);
    }
}
