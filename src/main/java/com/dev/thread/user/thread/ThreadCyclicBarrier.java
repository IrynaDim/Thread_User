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
        long start = System.nanoTime();

        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ExecutorService writers = Executors.newFixedThreadPool(2);
        executorService.execute(this::add);
        executorService.execute(this::add);

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        writers.execute(this::addToMySQL);
        writers.execute(this::addToMongoDB);
        writers.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Barrier: " + elapsed / 1000000);
        return getMap();
    }

    public void add() {
        super.addToMap();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
