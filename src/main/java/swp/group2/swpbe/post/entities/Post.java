package swp.group2.swpbe.post.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "file_url")
	private String fileUrl;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "updated_at")
	private Date updatedAt;
	@Column(name = "user_id")
	private String userId;
	private String content;

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<PostLike> likes;

	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
	@JsonManagedReference
	private List<Comments> comments;

	public Post() {
	}

	public Post(String fileUrl, String userId, String content) {
		this.fileUrl = fileUrl;
		this.createdAt = new Date();
		this.updatedAt = new Date();
		this.userId = userId;
		this.content = content;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileUrl() {
		return this.fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<PostLike> getLikes() {
		return this.likes;
	}

	public void setLikes(List<PostLike> likes) {
		this.likes = likes;
	}

	public List<Comments> getComments() {
		return this.comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

}
