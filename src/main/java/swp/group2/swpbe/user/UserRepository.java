package swp.group2.swpbe.user;
import org.springframework.stereotype.Repository;
import swp.group2.swpbe.constant.UserRole;
import swp.group2.swpbe.user.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int id);
    User findByEmail(String email);
    User findBySid(String s_id);
    User findByEmailAndSid(String email, String s_id);
    List<User> findByRole(UserRole role);
}