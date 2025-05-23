package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "views")
    private Integer views;

    @ManyToOne
    @JoinColumn(name = "test_level_id")
    private TestLevel testLevel;

    @Column(name = "instructor_name")
    private String instructorName;

    @Column(name = "instructor_title")
    private String instructorTitle;

    @Column(name = "instructor_experience")
    private String instructorExperience;

    @Column(name = "instructor_description", columnDefinition = "TEXT")
    private String instructorDescription;

    @Column(name = "instructor_avatar")
    private String instructorAvatar;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestPart> testParts = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestFeature> testFeatures = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestRequirement> testRequirements = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestReview> testReviews = new ArrayList<>();

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

    public void addTestReview(TestReview review) {
        testReviews.add(review);
        review.setTest(this);
    }

    public void removeTestReview(TestReview review) {
        testReviews.remove(review);
        review.setTest(null);
    }
}