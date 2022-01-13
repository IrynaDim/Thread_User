package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Queue;

@Data
public class ThreadUserJoin extends AbstractThread implements Runnable {

    public ThreadUserJoin(Queue<String> dataFromFile,
                          Map<String, User> map,
                          UserDaoJdbc userDaoJdbc,
                          UserDaoMongo userDaoMongo) {
        super(dataFromFile, map, userDaoJdbc, userDaoMongo);
    }

    @Override
    public void run() {
        addToMap();
        addToDb();
    }

    public void completionJoin(Thread t, Thread t1) throws InterruptedException {
        t.join();
        t1.join();
    }

    public void completionJoinArray(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
