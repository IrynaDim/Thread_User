package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ThreadCompletion extends AbstractThread {
    public ThreadCompletion(UserDaoJdbc userDaoJdbc,
                            UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        long start = System.nanoTime();

        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletionService<Map<String, User>> completionService = new ExecutorCompletionService<>(executorService);
        completionService.submit(this::add);
        completionService.submit(this::add);
        try {
            completionService.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.execute(this::addToMongoDB);
        executorService.execute(this::addToMySQL);
        executorService.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("CompletionService.take(): " + elapsed / 1000000);

        return getMap();
    }

    public Map<String, User> add() {
        this.addToMap();
        return null;
    }
}
