package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadUserCompletion extends AbstractThread implements Callable<Map<String, User>> {
    public ThreadUserCompletion(Queue<String> dataFromFile,
                                Map<String, User> map,
                                UserDaoJdbc userDaoJdbc,
                                UserDaoMongo userDaoMongo) {
        super(dataFromFile, map, userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> call() {
        addToMap();
        addToDb();
        return super.getMap();
    }

    @Override
    public Map<String, User> startThread() {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();

        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final CompletionService<Map<String, User>> service = new ExecutorCompletionService<>(pool);
        service.submit(new ThreadUserCompletion(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo()));
        service.submit(new ThreadUserCompletion(super.getDataFromFile(), map, super.getUserDaoJdbc(), super.getUserDaoMongo()));
        try {
            service.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("CompletionService: " + elapsed / 1000000);
        return map;
    }
}
