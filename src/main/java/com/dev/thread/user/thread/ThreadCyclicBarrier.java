package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadCyclicBarrier extends AbstractThread {
    private final CyclicBarrier barrier = new CyclicBarrier(THREADS_NUMBER);

    public ThreadCyclicBarrier(UserDaoJdbc userDaoJdbc,
                               UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        super.run();
        ExecutorService read = Executors.newFixedThreadPool(4);
        read.execute(this::addToMap);
        read.execute(this::addToMap);

        ExecutorService write = Executors.newFixedThreadPool(THREADS_NUMBER);
        read.execute(this::addToMySQL);
        read.execute(this::addToMongoDB);
        read.shutdown();
        return getMap();
    }

    @Override
    public void addToMap() {
        super.addToMap();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addToMySQL() {
        super.addToMySQL();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addToMongoDB() {
        super.addToMongoDB();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
