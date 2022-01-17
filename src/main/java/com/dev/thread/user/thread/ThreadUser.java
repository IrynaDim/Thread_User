package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadUser extends AbstractThread implements Runnable {
    public ThreadUser(Queue<String> dataFromFile,
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

    public void completionJoinArray(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Override
    public Map<String, User> startThread() {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();

        ThreadUser threadUser = new ThreadUser(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        Thread t = new Thread(threadUser);
        Thread t1 = new Thread(threadUser);
        t.start();
        t1.start();
        try {
            threadUser.completionJoinArray(Arrays.asList(t, t1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
        t1.interrupt();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Join: " + elapsed / 1000000);
        return map;
    }
}

