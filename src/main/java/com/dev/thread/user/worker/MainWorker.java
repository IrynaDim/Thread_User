package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;

import com.dev.thread.user.util.ThreadUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MainWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;
    private Map<String, User> map;


    public MainWorker(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    public void testThread(String version) throws InterruptedException{
        beforeThread();

        ThreadUser dataFromFileReading = new ThreadUser(FileReader.readFromFile("input.txt"), map);
        Thread t = new Thread(dataFromFileReading);
        t.setName("Thread-One");
        Thread t1 = new Thread(dataFromFileReading);
        t1.setName("Thread-Two");

        t.start();
        t1.start();

        t.join();
        t1.join();

        List<User> usersList = new ArrayList<>(map.values());

        userDaoJdbc.saveAll(usersList);
        userDaoMongo.saveAll(usersList);
    }

    private void beforeThread() {
        map = new HashMap<>();
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
