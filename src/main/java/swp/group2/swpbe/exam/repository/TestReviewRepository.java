package swp.group2.swpbe.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.TestReview;
import java.util.List;

@Repository
public interface TestReviewRepository extends JpaRepository<TestReview, Integer> {
    List<TestReview> findByTestId(Integer testId);

    void deleteByTestIdAndId(Integer testId, Integer reviewId);
}