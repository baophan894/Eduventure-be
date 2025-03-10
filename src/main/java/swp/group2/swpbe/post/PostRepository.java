package swp.group2.swpbe.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.post.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	Post findById(int id);

	List<Post> findAllByOrderByCreatedAtDesc();

	List<Post> findAllByUserIdOrderByCreatedAtDesc(String userId);

	Post findByIdAndUserId(int id, String userId);
}
