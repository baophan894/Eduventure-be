package swp.group2.swpbe.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestPartDTO {
    private Integer id;
    private String name;
    private String icon;
    private Integer duration;
    private String description;
    private List<QuestionDTO> questions;
}