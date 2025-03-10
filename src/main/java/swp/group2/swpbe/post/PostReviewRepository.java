package swp.group2.swpbe.post;

import org.springframework.stereotype.Repository;

import swp.group2.swpbe.post.entities.Post;
import swp.group2.swpbe.post.entities.PostLike;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PostReviewRepository extends JpaRepository<PostLike, Integer> {
    PostLike findByPostAndUserId(Post post, String userId);
    PostLike  findByPost(Post post);
}