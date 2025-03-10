package swp.group2.swpbe.post;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import swp.group2.swpbe.exception.ApiRequestException;
import swp.group2.swpbe.post.entities.Comments;
import swp.group2.swpbe.post.entities.Post;
import swp.group2.swpbe.post.entities.PostLike;

@Service
public class PostService {
	@Autowired
	PostRepository postRepository;

	@Autowired
	PostReviewRepository reviewRepository;

	@Autowired
	private CommentRepository commentRepository;

	public void createPost(String url, String content, String userId) {
		Post post = new Post(url, userId, content);
		postRepository.save(post);
	}

	public Post getPostById(String postId) {
		Post post = postRepository.findById(Integer.parseInt(postId));
		return post;
	}

	public void updatePost(String id, String url, String content, String userId) {
		Post post = postRepository.findById(Integer.parseInt(id));
		if (post == null) {
			throw new ApiRequestException("Post not found", HttpStatus.BAD_REQUEST);
		}
		if (!post.getUserId().equals(userId)) {
			throw new ApiRequestException("Unauthorized user", HttpStatus.UNAUTHORIZED);
		}
		post.setUpdatedAt(new Date());
		post.setContent(content);

		if (url != null)
			post.setFileUrl(url);
		postRepository.save(post);

	}

	public void deletePost(int id, String userId) {
		Post post = postRepository.findByIdAndUserId(id, userId);
		if (post == null) {
			throw new ApiRequestException("Post not found", HttpStatus.BAD_REQUEST);
		}
		postRepository.delete(post);
	}

	public List<Post> getAllPosts() {
		return postRepository.findAllByOrderByCreatedAtDesc();
	}

	public List<Post> getMyPost(String userId) {
		return postRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
	}

	public void uploadReview(Post post, String userId) {
		PostLike postReviewExist = reviewRepository.findByPostAndUserId(post, userId);
		if (postReviewExist != null) {
			reviewRepository.delete(postReviewExist);
			return;
		} else {
			PostLike postLike = new PostLike(userId, post);
			reviewRepository.save(postLike);
		}
	}

	public void addComment(String url, String userId, String content, String postId) {
		Post post = postRepository.findById(Integer.parseInt(postId));
		Comments c = new Comments(url, userId, content, post);
		commentRepository.save(c);
	}

	public Comments getCommentsById(String id) {
		return commentRepository.findById(Integer.parseInt(id));
	}

	public void updateComment(int id, String userId, String url, String content) {
		Comments comment = commentRepository.findByIdAndUserId(id, userId);
		if (comment == null) {
			throw new ApiRequestException("Comment not found", HttpStatus.BAD_REQUEST);
		}
		comment.setUpdatedAt(new Date());
		comment.setContent(content);
		if (url != null)
			comment.setFileUrl(url);
		commentRepository.save(comment);

	}

	public void deleteComment(int id, String userId) {
		Comments comment = commentRepository.findByIdAndUserId(id, userId);
		if (comment == null) {
			throw new ApiRequestException("Comment not found", HttpStatus.BAD_REQUEST);
		}
		commentRepository.delete(comment);
	}

}