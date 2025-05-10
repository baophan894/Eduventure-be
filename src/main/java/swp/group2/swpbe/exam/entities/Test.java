package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "type", nullable = false)
    private TestType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "views")
    private Integer views = 0;

    @Column(name = "ratings")
    private Float ratings = 0.0f;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "instructor_title")
    private String instructorTitle;

    @Column(name = "instructor_experience")
    private String instructorExperience;

    @Column(name = "instructor_description")
    private String instructorDescription;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestPart> testParts = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestFeature> testFeatures = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestRequirement> testRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestTargetScore> testTargetScores = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestReview> testReviews = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatedTest> relatedTests = new ArrayList<>();

    // Helper methods for collection management
    public void addTestPart(TestPart part) {
        testParts.add(part);
        part.setTest(this);
    }

    public void removeTestPart(TestPart part) {
        testParts.remove(part);
        part.setTest(null);
    }

    public void addTestFeature(TestFeature feature) {
        testFeatures.add(feature);
        feature.setTest(this);
    }

    public void removeTestFeature(TestFeature feature) {
        testFeatures.remove(feature);
        feature.setTest(null);
    }

    public void addTestRequirement(TestRequirement requirement) {
        testRequirements.add(requirement);
        requirement.setTest(this);
    }

    public void removeTestRequirement(TestRequirement requirement) {
        testRequirements.remove(requirement);
        requirement.setTest(null);
    }

    public void addTestTargetScore(TestTargetScore targetScore) {
        testTargetScores.add(targetScore);
        targetScore.setTest(this);
    }

    public void removeTestTargetScore(TestTargetScore targetScore) {
        testTargetScores.remove(targetScore);
        targetScore.setTest(null);
    }

    public void addTestReview(TestReview review) {
        testReviews.add(review);
        review.setTest(this);
    }

    public void removeTestReview(TestReview review) {
        testReviews.remove(review);
        review.setTest(null);
    }

    public void addRelatedTest(RelatedTest relatedTest) {
        relatedTests.add(relatedTest);
        relatedTest.setTest(this);
    }

    public void removeRelatedTest(RelatedTest relatedTest) {
        relatedTests.remove(relatedTest);
        relatedTest.setTest(null);
    }
}