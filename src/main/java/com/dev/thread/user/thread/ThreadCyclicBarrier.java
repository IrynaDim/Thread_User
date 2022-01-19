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
    private final CyclicBarrier barrierWrite = new CyclicBarrier(THREADS_NUMBER);

    public ThreadCyclicBarrier(UserDaoJdbc userDaoJdbc,
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
        read.shutdown();

        ExecutorService write = Executors.newFixedThreadPool(2);
        write.execute(this::addDB1);
        write.execute(this::addDB2);
        write.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Barrier: " + elapsed / 1000000);
        return getMap();
    }

    public void add() {
        super.addToMap();
        try {
            barrierWrite.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public void addDB1() {
        super.addToMySQL();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public void addDB2() {
        super.addToMongoDB();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
