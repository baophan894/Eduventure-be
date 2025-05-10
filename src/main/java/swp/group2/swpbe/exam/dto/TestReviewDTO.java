package swp.group2.swpbe.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestReviewDTO {
    private Integer id;
    private Integer testId;
    private Long userId;
    private Integer rating;
    private LocalDateTime reviewDate;
    private String comment;
}