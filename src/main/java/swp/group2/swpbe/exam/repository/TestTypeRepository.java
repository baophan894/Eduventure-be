package swp.group2.swpbe.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.TestType;
import java.util.Optional;

@Repository
public interface TestTypeRepository extends JpaRepository<TestType, Integer> {
    Optional<TestType> findByName(String name);
}