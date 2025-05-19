package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TestReviewFilterDTO {
    private Integer rating;
    private Long userId;
    private String comment;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}