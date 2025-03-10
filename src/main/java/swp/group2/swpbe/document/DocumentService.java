package swp.group2.swpbe.document;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swp.group2.swpbe.constant.ResourceStatus;
import swp.group2.swpbe.constant.ReviewState;
import swp.group2.swpbe.document.entities.Document;
import swp.group2.swpbe.document.entities.DocumentReview;

@Service
public class DocumentService {
    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    DocumentReviewRepository reviewRepository;

    public void uploadDocument(String url, String description, String userId, String topicId, String title) {
        Document document = new Document(url, title, description, userId, topicId);
        documentRepository.save(document);

    }

    public Document getDocumentById(String documentId) {
        Document document = documentRepository.findById(Integer.parseInt(documentId));
        return document;
    }

    public List<Document> getAllDocuments() {
        List<Document> documents = documentRepository.findAll();
        Collections.sort(documents, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                long countHelpful1 = o1.getReviews().stream()
                        .filter(review -> ReviewState.helpful.equals(review.getState()))
                        .count();
                long countHelpful2 = o2.getReviews().stream()
                        .filter(review -> ReviewState.unhelpful.equals(review.getState()))
                        .count();
                return Long.compare(countHelpful2, countHelpful1);
            }
        });
        return documents;
    }

    public void uploadReview(Document document, ReviewState review, String userId) {
        DocumentReview documentReviewExist = reviewRepository.findByDocumentAndUserId(document, userId);
        if (documentReviewExist != null) {
            if (documentReviewExist.getState().equals(review)) {
                reviewRepository.delete(documentReviewExist);
                return;
            } else {
                documentReviewExist.setState(review);
                reviewRepository.save(documentReviewExist);
                return;
            }

        }
        DocumentReview documentReview = new DocumentReview(review, userId, document);
        reviewRepository.save(documentReview);
    }

    public void updateDocumentState(Document document, ResourceStatus state) {
        document.setState(state);
        documentRepository.save(document);
    }

}
