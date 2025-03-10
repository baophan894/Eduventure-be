package swp.group2.swpbe.user.dto;

public class SignupDTO {

    private String full_name;
    private String email;
    private String password;

    public SignupDTO() {

    }

    public SignupDTO(String full_name, String email, String password) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
    }

    public String getFull_name() {
        return this.full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
