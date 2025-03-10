package swp.group2.swpbe.topic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swp.group2.swpbe.topic.enties.Topic;

@Service
public class TopicService {
    @Autowired
    TopicRepository topicRepository;

    public List<Topic> getAllTopic() {
        return topicRepository.findAll();
    }

    public Topic createTopic(String name) {
        Topic topic = new Topic();
        topic.setName(name);
        return topicRepository.save(topic);
    }

    public Topic updateTopic(int id, String newName) {
        Topic topic = topicRepository.findById(id);
        topic.setName(newName);
        return topicRepository.save(topic);
    }

    public void deleteTopic(int id) {
        topicRepository.deleteById(id);
    }
}
