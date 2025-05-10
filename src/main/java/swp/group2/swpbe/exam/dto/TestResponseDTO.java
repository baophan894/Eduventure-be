package swp.group2.swpbe.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDTO {
    private List<TestListDTO> tests;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;
}