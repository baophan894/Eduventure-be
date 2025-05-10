package swp.group2.swpbe.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
}