package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

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
    }

    @Override
    public void run() {
        try {
            addToMap();
            addToDb();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
