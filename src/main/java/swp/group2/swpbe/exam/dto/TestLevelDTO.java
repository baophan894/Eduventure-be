package swp.group2.swpbe.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestLevelDTO {
    private Integer id;
    private String name;
    private LanguageDTO language;
}