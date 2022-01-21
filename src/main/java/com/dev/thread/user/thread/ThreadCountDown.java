package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadCountDown extends AbstractThread {
   private final CountDownLatch latchRead = new CountDownLatch(THREADS_NUMBER);
    private final CountDownLatch latchWrite = new CountDownLatch(THREADS_NUMBER);

    public ThreadCountDown(UserDaoJdbc userDaoJdbc,
                           UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        super.run();
        ExecutorService executorRead = Executors.newFixedThreadPool(4);
        executorRead.execute(this::addToMap);
        executorRead.execute(this::addToMap);
        try {
            latchRead.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
   //     executorRead.shutdown();

        ExecutorService executorWrite = Executors.newFixedThreadPool(THREADS_NUMBER);
        executorRead.execute(this::addToMySQL);
        executorRead.execute(this::addToMySQL);
        try {
            latchWrite.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorRead.shutdown();

        return getMap();
    }

    @Override
    public void addToMap() {
        super.addToMap();
        latchRead.countDown();
    }

    @Override
    public void addToMySQL() {
        super.addToMySQL();
        latchWrite.countDown();
    }

    @Override
    public void addToMongoDB() {
        super.addToMongoDB();
        latchWrite.countDown();
    }
}

