package com.dev.thread.user.dao;

import com.dev.thread.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoMongo {
    private final UserRepository userRepository;

    public UserDaoMongo(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void saveAll(List<User> users) {
        userRepository.saveAll(users);
    }

    public void clearTable() {
        userRepository.deleteAll();
    }
}
