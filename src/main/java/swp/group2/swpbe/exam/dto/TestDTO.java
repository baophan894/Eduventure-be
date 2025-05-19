package swp.group2.swpbe.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
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
    private LocalDateTime lastUpdated;
    private String instructorName;
    private String instructorTitle;
    private String instructorExperience;
    private String instructorDescription;
    private String instructorAvatar;
    private List<String> testFeatures;
    private List<String> testRequirements;
    private List<String> testTargetScores;
    private List<TestPartDTO> testParts;
}