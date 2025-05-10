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
    private List<String> testFeatures;
    private List<String> testRequirements;
    private List<String> testTargetScores;

    public TestListDTO(Integer id, Integer typeId, String title, String description, String coverImg,
            Integer views, Float ratings, Integer reviewCount, Integer duration, String difficulty,
            LocalDateTime lastUpdated, String instructorName, String instructorTitle) {
        this.id = id;
        this.typeId = typeId;
        this.title = title;
        this.description = description;
        this.coverImg = coverImg;
        this.views = views;
        this.ratings = ratings;
        this.reviewCount = reviewCount;
        this.duration = duration;
        this.difficulty = difficulty;
        this.lastUpdated = lastUpdated;
        this.instructorName = instructorName;
        this.instructorTitle = instructorTitle;
    }
}