package com.dev.thread.user.service;

import com.dev.thread.user.dao.mongo.SequenceDao;
import com.dev.thread.user.dao.mongo.UserDao;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final SequenceDao sequenceDao;
    private final UserDao contactDao;

    public UserService(SequenceDao sequenceDao, UserDao contactDao) {
        this.sequenceDao = sequenceDao;
        this.contactDao = contactDao;
    }

    public void add(User user) {
      //  user.setId(sequenceDao.getNextSequenceId(User.COLLECTION_NAME));
        contactDao.save(user);
    }
    public void update(User user) {
        contactDao.save(user);
    }

    public User get(Long id) {
        return contactDao.get(id);
    }

    public List<User> getAll() {
        return contactDao.getAll();
    }
}
