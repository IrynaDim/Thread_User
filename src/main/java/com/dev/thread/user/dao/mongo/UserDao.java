package com.dev.thread.user.dao.mongo;

import com.dev.thread.user.model.User;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {
    private final MongoOperations userRepository;

    public UserDao(MongoOperations userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User get(Long id) {
        return userRepository.findOne(Query.query(Criteria.where("user_id").is(id)), User.class);
    }

    public List<User> getAll() {
        return userRepository.findAll(User.class);
    }
}
