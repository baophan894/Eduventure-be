package swp.group2.swpbe.exam.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestReviewResponseDTO {
    private List<TestReviewDTO> reviews;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    private int pageSize;

    // Rating statistics
    private double averageRating;
    private long totalReviews;
    private long fiveStarCount;
    private long fourStarCount;
    private long threeStarCount;
    private long twoStarCount;
    private long oneStarCount;
}