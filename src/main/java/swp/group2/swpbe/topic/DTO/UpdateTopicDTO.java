package swp.group2.swpbe.topic.DTO;

public class UpdateTopicDTO {
    String newName;

    public UpdateTopicDTO(String newName) {
        this.newName = newName;
    }
    

    public UpdateTopicDTO() {
    }

    public String getNewName() {
        return this.newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

}
