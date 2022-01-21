package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ThreadJoin extends AbstractThread {
    public ThreadJoin(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        long start = System.nanoTime();

        super.run();
        Thread tRead = new Thread(this::addToMap);
        Thread t1Read = new Thread(this::addToMap);
        tRead.start();
        t1Read.start();
        try {
            tRead.join();
            t1Read.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread tWrite = new Thread(this::addToMongoDB);
        Thread t1Write = new Thread(this::addToMySQL);
        tWrite.start();
        t1Write.start();
        try {
            tWrite.join();
            t1Write.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Join: " + elapsed / 1000000);
        return getMap();
    }
}

