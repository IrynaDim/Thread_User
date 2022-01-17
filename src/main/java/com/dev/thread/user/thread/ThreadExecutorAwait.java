package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadExecutorAwait extends AbstractThread implements Runnable {
    public ThreadExecutorAwait(Queue<String> dataFromFile,
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

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public Map<String, User> startThread() {
        long start = System.nanoTime();
        Map<String, User> map = new HashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadExecutorAwait t = new ThreadExecutorAwait(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        ThreadExecutorAwait t1 = new ThreadExecutorAwait(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        executorService.submit(t);
        executorService.submit(t1);
        t.awaitTerminationAfterShutdown(executorService);
        t1.awaitTerminationAfterShutdown(executorService);

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor2: " + elapsed / 1000000);
        return map;
    }
}
