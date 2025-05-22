package swp.group2.swpbe.exam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.exam.entities.TestReview;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestReviewRepository extends JpaRepository<TestReview, Integer> {
        Page<TestReview> findByTestId(Integer testId, Pageable pageable);

        @Query("SELECT r FROM TestReview r WHERE r.test.id = :testId " +
                        "AND (:rating IS NULL OR r.rating = :rating) " +
                        "AND (:userId IS NULL OR r.userId = :userId) " +
                        "AND (:comment IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) " +
                        "AND (:fromDate IS NULL OR r.reviewDate >= :fromDate) " +
                        "AND (:toDate IS NULL OR r.reviewDate <= :toDate)")
        Page<TestReview> findByTestIdAndFilters(
                        @Param("testId") Integer testId,
                        @Param("rating") Integer rating,
                        @Param("userId") Long userId,
                        @Param("comment") String comment,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        Pageable pageable);

        @Query("SELECT COUNT(r) FROM TestReview r WHERE r.test.id = :testId " +
                        "AND (:rating IS NULL OR r.rating = :rating) " +
                        "AND (:userId IS NULL OR r.userId = :userId) " +
                        "AND (:comment IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) " +
                        "AND (:fromDate IS NULL OR r.reviewDate >= :fromDate) " +
                        "AND (:toDate IS NULL OR r.reviewDate <= :toDate)")
        long countByTestIdAndFilters(
                        @Param("testId") Integer testId,
                        @Param("rating") Integer rating,
                        @Param("userId") Long userId,
                        @Param("comment") String comment,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        @Query("SELECT AVG(r.rating) FROM TestReview r WHERE r.test.id = :testId " +
                        "AND (:rating IS NULL OR r.rating = :rating) " +
                        "AND (:userId IS NULL OR r.userId = :userId) " +
                        "AND (:comment IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) " +
                        "AND (:fromDate IS NULL OR r.reviewDate >= :fromDate) " +
                        "AND (:toDate IS NULL OR r.reviewDate <= :toDate)")
        Double getAverageRatingByTestIdAndFilters(
                        @Param("testId") Integer testId,
                        @Param("rating") Integer rating,
                        @Param("userId") Long userId,
                        @Param("comment") String comment,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        @Query("SELECT COUNT(r) FROM TestReview r WHERE r.test.id = :testId " +
                        "AND r.rating = :rating " +
                        "AND (:userId IS NULL OR r.userId = :userId) " +
                        "AND (:comment IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) " +
                        "AND (:fromDate IS NULL OR r.reviewDate >= :fromDate) " +
                        "AND (:toDate IS NULL OR r.reviewDate <= :toDate)")
        long countByTestIdAndRatingAndFilters(
                        @Param("testId") Integer testId,
                        @Param("rating") Integer rating,
                        @Param("userId") Long userId,
                        @Param("comment") String comment,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        List<TestReview> findByTestId(Integer testId);

        void deleteByTestIdAndId(Integer testId, Integer reviewId);

        @Query("SELECT COUNT(r) FROM TestReview r WHERE r.test.id = :testId AND r.rating = :rating")
        long countByTestIdAndRating(@Param("testId") Integer testId, @Param("rating") Integer rating);

        @Query("SELECT AVG(r.rating) FROM TestReview r WHERE r.test.id = :testId")
        Double getAverageRatingByTestId(@Param("testId") Integer testId);

        @Query("SELECT COUNT(r) FROM TestReview r WHERE r.test.id = :testId")
        long countByTestId(@Param("testId") Integer testId);

        TestReview findByTestIdAndUserId(Integer testId, Long userId);
}