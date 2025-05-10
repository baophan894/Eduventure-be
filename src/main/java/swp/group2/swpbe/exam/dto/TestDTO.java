package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TestDTO {
    private Integer id;
    private Integer typeId;
    private String title;
    private String description;
    private String coverImg;
    private Integer views;
    private Float ratings;
    private Integer reviewCount;
    private Integer duration;
    private String difficulty;
    private LocalDateTime lastUpdated;
    private String instructorName;
    private String instructorTitle;
    private String instructorExperience;
    private String instructorDescription;
    private List<TestPartDTO> testParts;
}