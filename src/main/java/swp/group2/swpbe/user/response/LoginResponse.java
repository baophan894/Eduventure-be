package swp.group2.swpbe.user.response;

import swp.group2.swpbe.constant.UserRole;

public class LoginResponse {
    private String token;
    private UserRole role;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserRole role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

}
