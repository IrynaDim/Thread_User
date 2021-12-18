package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MainWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;
    private final String FILE_NAME = "input.txt";
    private final ThreadWorker threadWorker;

    public MainWorker(UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo, ThreadWorker threadWorker) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
        this.threadWorker = threadWorker;
    }

    public void testThread(String version) {
        beforeThread();

        List<User> usersList = threadWorker.chooseVersion(version, FILE_NAME);

        userDaoJdbc.saveAll(usersList);
        userDaoMongo.saveAll(usersList);
    }

    private void beforeThread() {
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
