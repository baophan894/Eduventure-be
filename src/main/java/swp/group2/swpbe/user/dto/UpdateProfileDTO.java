package swp.group2.swpbe.user.dto;

import java.util.Date;

public class UpdateProfileDTO {
    private String fullName;
    private String about;
    private Date dob;
    private String gender;


    public UpdateProfileDTO() {
    }

    public UpdateProfileDTO(String fullName, String about, Date dob, String gender) {
        this.fullName = fullName;
        this.about = about;
        this.dob = dob;
        this.gender = gender;
    }


    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAbout() {
        return this.about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Date getDob() {
        return this.dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

   
}
