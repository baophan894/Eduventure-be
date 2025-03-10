package swp.group2.swpbe.course.response;

import java.util.Date;

public class StudentResponse {
    private String fullName;
    private String avatarUrl;
    private Date joinAt;
    private int userId;

    public StudentResponse() {
    }

    public StudentResponse(String fullName, String avatarUrl, int userId, Date joinAt) {
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
        this.joinAt = joinAt;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getJoinAt() {
        return this.joinAt;
    }

    public void setJoinAt(Date joinAt) {
        this.joinAt = joinAt;
    }

}
