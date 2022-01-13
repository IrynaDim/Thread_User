package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;

import org.springframework.stereotype.Service;


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
        threadWorker.chooseVersion(version, FILE_NAME);

//        userDaoJdbc.saveAll(usersList);
//        userDaoMongo.saveAll(usersList);
    }

    private void beforeThread() {
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
