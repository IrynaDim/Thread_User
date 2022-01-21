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
   private final CountDownLatch countDownLatchWrite = new CountDownLatch(THREADS_NUMBER);
    private final CountDownLatch countDownLatchRead = new CountDownLatch(THREADS_NUMBER);

    public ThreadCountDown(UserDaoJdbc userDaoJdbc,
                           UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        long start = System.nanoTime();

        super.run();
        ExecutorService read = Executors.newFixedThreadPool(2);
        read.execute(this::add);
        read.execute(this::add);
        try {
            countDownLatchWrite.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        read.shutdown();

        ExecutorService write = Executors.newFixedThreadPool(2);
        write.execute(this::addToDb1);
        write.execute(this::addToDb2);
        try {
            countDownLatchRead.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Count down: " + elapsed / 1000000);
        return getMap();
    }

    public void add() {
        super.addToMap();
        countDownLatchWrite.countDown();
    }

    public void addToDb1() {
        super.addToMySQL();
        countDownLatchRead.countDown();
    }

    public void addToDb2() {
        super.addToMongoDB();
        countDownLatchRead.countDown();
    }
}

