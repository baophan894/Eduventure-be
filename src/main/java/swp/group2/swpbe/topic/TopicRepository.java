package swp.group2.swpbe.topic;


import org.springframework.stereotype.Repository;

import swp.group2.swpbe.topic.enties.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Topic findById(int id);
}