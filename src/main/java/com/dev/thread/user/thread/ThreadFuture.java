package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;

import java.util.*;
import java.util.concurrent.*;

public class ThreadFuture extends AbstractThread implements Runnable {
    public ThreadFuture(Queue<String> dataFromFile,
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

    @Override
    public Map<String, User> startThread() {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(new ThreadUser(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo())));
        futures.add(executorService.submit(new ThreadUser(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo())));
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor: " + elapsed / 1000000);
        return map;
    }
}
