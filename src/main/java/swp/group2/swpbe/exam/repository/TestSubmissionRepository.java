package swp.group2.swpbe.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.TestSubmission;
import java.util.List;

@Repository
public interface TestSubmissionRepository extends JpaRepository<TestSubmission, Integer> {
    Page<TestSubmission> findByUserId(Long userId, Pageable pageable);

    Page<TestSubmission> findByTestId(Integer testId, Pageable pageable);

    Page<TestSubmission> findByUserIdAndTestId(Long userId, Integer testId, Pageable pageable);

    List<TestSubmission> findByTestId(Integer testId);
}