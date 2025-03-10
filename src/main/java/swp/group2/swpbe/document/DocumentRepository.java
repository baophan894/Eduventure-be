package swp.group2.swpbe.document;

import org.springframework.stereotype.Repository;

import swp.group2.swpbe.document.entities.Document;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    Document findById(int documentId);
     List<Document> findByUserId(String userId);
}
