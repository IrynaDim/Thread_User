package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.worker.FileReader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadUser extends AbstractThread implements Runnable {
    public ThreadUser(Queue<String> dataFromFile,
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

    public void completionJoinArray(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Override
    public Map<String, User> startThread(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ThreadUser threadUser = new ThreadUser(dataFromFile, map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        Thread t = new Thread(threadUser);
        Thread t1 = new Thread(threadUser);
        t.start();
        t1.start();
        try {
            threadUser.completionJoinArray(Arrays.asList(t, t1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
        t1.interrupt();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Join: " + elapsed / 1000000);
        return map;
    }
}

