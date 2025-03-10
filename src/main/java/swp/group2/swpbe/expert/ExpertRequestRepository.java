
package swp.group2.swpbe.expert;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import swp.group2.swpbe.expert.entities.ExpertRequest;

@Repository
public interface ExpertRequestRepository extends JpaRepository<ExpertRequest, Integer> {

    ExpertRequest findById(int id);

    List<ExpertRequest> findAllByOrderByCreatedAtDesc();

    List<ExpertRequest> findByUserId(String userId);
}
