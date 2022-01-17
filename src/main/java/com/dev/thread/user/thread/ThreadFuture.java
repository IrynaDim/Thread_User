package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class ThreadFuture extends AbstractThread {
    public ThreadFuture(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        long start = System.nanoTime();

        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(this::addToMap));
        futures.add(executorService.submit(this::addToMap));
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.submit(this::addToMongoDB);
        executorService.submit(this::addToMySQL);
        executorService.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Future.get(): " + elapsed / 1000000);
        return getMap();
    }
}
