package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ThreadUserCyclicBarrier extends AbstractThread implements Runnable {
    private CyclicBarrier barrier;

    public ThreadUserCyclicBarrier(CyclicBarrier barrier,
                                   Queue<String> dataFromFile,
                                   Map<String, User> map,
                                   UserDaoJdbc userDaoJdbc,
                                   UserDaoMongo userDaoMongo) {
        super(dataFromFile, map, userDaoJdbc, userDaoMongo);
        this.barrier = barrier;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            addToMap();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        addToDb();
    }

    @Override
    public Map<String, User> startThread() {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();

        CyclicBarrier barrier = new CyclicBarrier(2);
        new ThreadUserCyclicBarrier(barrier, super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        new ThreadUserCyclicBarrier(barrier, super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Barrier: " + elapsed / 1000000);
        return map;
    }
}
