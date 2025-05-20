package swp.group2.swpbe.exam.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestListDTO {
    private Integer id;
    private Integer typeId;
    private String typeName;
    private String title;
    private String description;
    private String coverImg;
    private Integer views;
    private Double ratings;
    private Integer reviewCount;
    private Integer duration;
    private String testLevel;
    private Integer testLevelId;
    private Integer languageId;
    private String languageName;
    private LocalDateTime lastUpdated;
    private String instructorName;
    private String instructorTitle;
    private String instructorDescription;
    private String instructorExperience;
    private String instructorAvatar;
    private List<String> testFeatures;
    private List<String> testRequirements;

    public TestListDTO(Integer id, Integer typeId, String title, String description, String coverImg,
            Integer views, Integer duration, String testLevel, LocalDateTime lastUpdated,
            String instructorName, String instructorTitle) {
        this.id = id;
        this.typeId = typeId;
        this.title = title;
        this.description = description;
        this.coverImg = coverImg;
        this.views = views;
        this.duration = duration;
        this.testLevel = testLevel;
        this.lastUpdated = lastUpdated;
        this.instructorName = instructorName;
        this.instructorTitle = instructorTitle;
    }

    public TestListDTO(Integer id, String title, String description, String coverImg,
            Integer views, Integer duration, String testLevel, LocalDateTime lastUpdated,
            String instructorName, String instructorTitle, String instructorAvatar) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.coverImg = coverImg;
        this.views = views;
        this.duration = duration;
        this.testLevel = testLevel;
        this.lastUpdated = lastUpdated;
        this.instructorName = instructorName;
        this.instructorTitle = instructorTitle;
        this.instructorAvatar = instructorAvatar;
    }
}