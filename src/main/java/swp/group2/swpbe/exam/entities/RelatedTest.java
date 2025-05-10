package swp.group2.swpbe.exam.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "related_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatedTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "related_test_id", nullable = false)
    private Integer relatedTestId;

    @Column(name = "related_test_title", nullable = false)
    private String relatedTestTitle;
}