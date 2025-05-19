package swp.group2.swpbe.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Integer> {
}