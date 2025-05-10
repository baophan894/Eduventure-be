package swp.group2.swpbe.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.Test;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    Page<Test> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}