package swp.group2.swpbe.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.Test;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    Page<Test> findByTitleContainingIgnoreCase(String search, Pageable pageable);

    Page<Test> findByTestLevelId(Integer testLevelId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelId(String search, Integer testLevelId, Pageable pageable);

    Page<Test> findByTypeId(Integer typeId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTypeId(String search, Integer typeId, Pageable pageable);

    Page<Test> findByTestLevelIdAndTypeId(Integer testLevelId, Integer typeId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelIdAndTypeId(String search, Integer testLevelId,
            Integer typeId, Pageable pageable);

    // New methods for language search
    Page<Test> findByTestLevelLanguageId(Integer languageId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelLanguageId(String search, Integer languageId,
            Pageable pageable);

    Page<Test> findByTestLevelLanguageIdAndTypeId(Integer languageId, Integer typeId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelLanguageIdAndTypeId(String search, Integer languageId,
            Integer typeId, Pageable pageable);

    Page<Test> findByTestLevelIdAndTestLevelLanguageId(Integer testLevelId, Integer languageId, Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageId(String search, Integer testLevelId,
            Integer languageId, Pageable pageable);

    Page<Test> findByTestLevelIdAndTestLevelLanguageIdAndTypeId(Integer testLevelId, Integer languageId, Integer typeId,
            Pageable pageable);

    Page<Test> findByTitleContainingIgnoreCaseAndTestLevelIdAndTestLevelLanguageIdAndTypeId(String search,
            Integer testLevelId, Integer languageId, Integer typeId, Pageable pageable);
}