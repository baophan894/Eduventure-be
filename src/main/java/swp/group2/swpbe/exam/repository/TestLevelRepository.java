package swp.group2.swpbe.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.TestLevel;
import java.util.Optional;

@Repository
public interface TestLevelRepository extends JpaRepository<TestLevel, Integer> {
    Optional<TestLevel> findByName(String name);
}