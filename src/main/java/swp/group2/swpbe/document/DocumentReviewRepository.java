package swp.group2.swpbe.document;

import org.springframework.stereotype.Repository;

import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.document.entities.Document;
import swp.group2.swpbe.document.entities.DocumentReview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
@Repository
public interface DocumentReviewRepository extends JpaRepository<DocumentReview, Integer> {
    DocumentReview findByDocumentAndUserId(Document document, String userId);
    DocumentReview findByDocument(Document document);
    
    long countByState(@Param("state") ReviewState state);
}

